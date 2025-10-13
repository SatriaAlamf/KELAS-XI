package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAddBlogBinding
import com.example.myapplication.models.Blog
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

/**
 * Add Blog Activity
 * Untuk membuat blog baru dengan gambar, judul, dan konten
 */
class AddBlogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBlogBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivBlogImage.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        // Select Image
        binding.fabSelectImage.setOnClickListener {
            openImagePicker()
        }

        binding.ivBlogImage.setOnClickListener {
            openImagePicker()
        }

        // Publish Blog
        binding.btnPublish.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (validateInput(title, content)) {
                publishBlog(title, content)
            }
        }

        // Cancel
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    /**
     * Open image picker
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    /**
     * Validasi input
     */
    private fun validateInput(title: String, content: String): Boolean {
        // Check image
        if (selectedImageUri == null) {
            AppUtils.showToast(this, "Please select an image")
            return false
        }

        // Check title
        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            binding.etTitle.requestFocus()
            return false
        }

        if (title.length < 5) {
            binding.etTitle.error = "Title must be at least 5 characters"
            binding.etTitle.requestFocus()
            return false
        }

        // Check content
        if (content.isEmpty()) {
            binding.etContent.error = "Content is required"
            binding.etContent.requestFocus()
            return false
        }

        if (content.length < 20) {
            binding.etContent.error = "Content must be at least 20 characters"
            binding.etContent.requestFocus()
            return false
        }

        // Check internet
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showToast(this, Constants.ERROR_NETWORK)
            return false
        }

        return true
    }

    /**
     * Publish blog ke Firestore
     */
    private fun publishBlog(title: String, content: String) {
        showLoading(true)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            showLoading(false)
            AppUtils.showToast(this, "User not authenticated")
            return
        }

        // Step 1: Upload image ke Storage
        uploadImageToStorage { imageUrl ->
            if (imageUrl != null) {
                // Step 2: Save blog data ke Firestore
                saveBlogToFirestore(title, content, imageUrl, currentUser.uid)
            } else {
                showLoading(false)
                AppUtils.showToast(this, "Failed to upload image")
            }
        }
    }

    /**
     * Upload image ke Firebase Storage
     */
    private fun uploadImageToStorage(onComplete: (String?) -> Unit) {
        val imageUri = selectedImageUri ?: return

        // Generate unique filename
        val filename = "blog_${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference
            .child(Constants.STORAGE_BLOG_IMAGES)
            .child(filename)

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }.addOnFailureListener {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Save blog data ke Firestore
     */
    private fun saveBlogToFirestore(
        title: String,
        content: String,
        imageUrl: String,
        authorId: String
    ) {
        val currentUser = auth.currentUser ?: return

        // Create blog object
        val blogId = firestore.collection(Constants.COLLECTION_BLOGS).document().id
        val blog = Blog(
            blogId = blogId,
            title = title,
            content = content,
            authorId = authorId,
            authorName = currentUser.displayName ?: "Anonymous",
            authorEmail = currentUser.email ?: "",
            timestamp = Timestamp.now(),
            imageUrl = imageUrl,
            likeCount = 0
        )

        // Save to Firestore
        firestore.collection(Constants.COLLECTION_BLOGS)
            .document(blogId)
            .set(blog)
            .addOnSuccessListener {
                showLoading(false)
                AppUtils.showToast(this, Constants.SUCCESS_BLOG_CREATED)
                finish() // Kembali ke MainActivity
            }
            .addOnFailureListener { e ->
                showLoading(false)
                AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
            }
    }

    /**
     * Show/hide loading
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnPublish.isEnabled = !isLoading
        binding.btnCancel.isEnabled = !isLoading
        binding.fabSelectImage.isEnabled = !isLoading
    }
}
