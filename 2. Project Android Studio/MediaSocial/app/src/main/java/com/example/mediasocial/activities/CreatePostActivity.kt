package com.example.mediasocial.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mediasocial.MainActivity
import com.example.mediasocial.databinding.ActivityCreatePostBinding
import com.example.mediasocial.models.Notification
import com.example.mediasocial.models.NotificationType
import com.example.mediasocial.models.Post
import com.example.mediasocial.models.User
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var selectedImageUri: Uri? = null
    private var currentUser: User? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivImagePreview.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupToolbar()
        loadCurrentUser()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadCurrentUser() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                currentUser = document.toObject(User::class.java)
            }
    }

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.btnUpload.setOnClickListener {
            uploadPost()
        }
    }

    private fun checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.REQUEST_STORAGE_PERMISSION
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                ToastUtils.showShort(this, "Izin akses storage diperlukan")
            }
        }
    }

    private fun uploadPost() {
        val description = binding.etDescription.text.toString().trim()

        if (selectedImageUri == null) {
            ToastUtils.showShort(this, "Pilih gambar terlebih dahulu")
            return
        }

        if (description.isEmpty()) {
            binding.tilDescription.error = "Deskripsi tidak boleh kosong"
            return
        }

        showLoading(true)

        // Upload image to Firebase Storage
        val imageRef = storage.reference
            .child(Constants.STORAGE_POSTS)
            .child("${auth.currentUser?.uid}_${UUID.randomUUID()}.jpg")

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    savePostToFirestore(downloadUri.toString(), description)
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                ToastUtils.showLong(this, "Gagal upload gambar: ${exception.message}")
            }
    }

    private fun savePostToFirestore(imageUrl: String, description: String) {
        val user = currentUser ?: return
        val postId = firestore.collection(Constants.COLLECTION_POSTS).document().id

        val post = Post(
            postId = postId,
            userId = user.uid,
            username = user.username,
            userProfileImage = user.profileImageUrl,
            imageUrl = imageUrl,
            description = description,
            timestamp = System.currentTimeMillis(),
            likes = emptyList(),
            likesCount = 0,
            commentsCount = 0
        )

        firestore.collection(Constants.COLLECTION_POSTS)
            .document(postId)
            .set(post)
            .addOnSuccessListener {
                showLoading(false)
                ToastUtils.showShort(this, "Postingan berhasil dibuat!")
                
                // Navigate back to main screen
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                ToastUtils.showLong(this, "Gagal menyimpan post: ${exception.message}")
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUpload.isEnabled = !isLoading
        binding.btnSelectImage.isEnabled = !isLoading
        binding.etDescription.isEnabled = !isLoading
    }
}
