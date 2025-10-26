package com.example.onlineimageapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.onlineimageapp.databinding.ActivityImageDetailBinding
import com.example.onlineimageapp.utils.FileUtils
import com.example.onlineimageapp.utils.PermissionUtils
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class ImageDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityImageDetailBinding
    private var imageUrl: String = ""
    private var imageTitle: String = ""
    private var imageDescription: String = ""
    private var imageId: String = ""
    private var imageTags: List<String> = emptyList()
    private var isFavorite: Boolean = false
    private var pendingBitmap: Bitmap? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            pendingBitmap?.let { bitmap ->
                saveImageToGallery(bitmap)
                pendingBitmap = null
            }
        } else {
            Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_LONG).show()
        }
    }
    
    companion object {
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_IMAGE_TITLE = "extra_image_title"
        const val EXTRA_IMAGE_DESCRIPTION = "extra_image_description"
        const val EXTRA_IMAGE_ID = "extra_image_id"
        const val EXTRA_IMAGE_TAGS = "extra_image_tags"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        getIntentData()
        setupToolbar()
        setupImageView()
        setupButtons()
        displayImageDetails()
    }
    
    private fun getIntentData() {
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL) ?: ""
        imageTitle = intent.getStringExtra(EXTRA_IMAGE_TITLE) ?: "Untitled"
        imageDescription = intent.getStringExtra(EXTRA_IMAGE_DESCRIPTION) ?: "No description available"
        imageId = intent.getStringExtra(EXTRA_IMAGE_ID) ?: ""
        imageTags = intent.getStringArrayListExtra(EXTRA_IMAGE_TAGS) ?: emptyList()
    }
    
    private fun setupToolbar() {
        binding.toolbar.apply {
            title = imageTitle
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
    
    private fun setupImageView() {
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.photoView.setImageDrawable(resource)
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle cleanup if needed
                    }
                })
        }
    }
    
    private fun setupButtons() {
        binding.btnShare.setOnClickListener {
            shareImage()
        }
        
        binding.btnDownload.setOnClickListener {
            downloadImage()
        }
        
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }
    
    private fun displayImageDetails() {
        binding.tvTitle.text = imageTitle
        binding.tvDescription.text = imageDescription
        
        // Add tags as chips
        binding.chipGroup.removeAllViews()
        imageTags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isClickable = false
                isCheckable = false
            }
            binding.chipGroup.addView(chip)
        }
    }
    
    private fun shareImage() {
        // First load the image as bitmap
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareImageBitmap(resource)
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
    
    private fun shareImageBitmap(bitmap: Bitmap) {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                imageTitle,
                imageDescription
            )
            val imageUri = Uri.parse(path)
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Check out this image: $imageTitle")
                putExtra(Intent.EXTRA_SUBJECT, imageTitle)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: Exception) {
            // Fallback to sharing URL
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out this image: $imageTitle\n$imageUrl")
                putExtra(Intent.EXTRA_SUBJECT, imageTitle)
            }
            
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }
    }
    
    private fun downloadImage() {
        if (PermissionUtils.isStoragePermissionGranted(this)) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        saveImageToGallery(resource)
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            // Request permissions first
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        pendingBitmap = resource
                        requestStoragePermission()
                    }
                    
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }
    
    private fun requestStoragePermission() {
        val permissions = PermissionUtils.getStoragePermissions()
        requestPermissionLauncher.launch(permissions)
    }
    
    private fun saveImageToGallery(bitmap: Bitmap) {
        val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.saveImageToGallery(this, bitmap, imageTitle, imageDescription)
        } else {
            FileUtils.saveImageLegacy(this, bitmap, imageTitle)
        }
        
        if (success) {
            Snackbar.make(binding.root, "Image saved to gallery", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(binding.root, "Failed to save image", Snackbar.LENGTH_LONG).show()
        }
    }
    
    private fun toggleFavorite() {
        isFavorite = !isFavorite
        updateFavoriteButton()
        
        val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    
    private fun updateFavoriteButton() {
        val iconRes = if (isFavorite) {
            com.example.onlineimageapp.R.drawable.ic_favorite
        } else {
            com.example.onlineimageapp.R.drawable.ic_favorite_border
        }
        binding.btnFavorite.setIconResource(iconRes)
    }
}