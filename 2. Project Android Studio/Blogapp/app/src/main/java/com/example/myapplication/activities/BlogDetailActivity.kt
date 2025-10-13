package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityBlogDetailBinding
import com.example.myapplication.models.Blog
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Blog Detail Activity
 * Menampilkan detail lengkap dari sebuah blog
 */
class BlogDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlogDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private var currentBlog: Blog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get blog ID from intent
        val blogId = intent.getStringExtra(Constants.EXTRA_BLOG_ID)
        if (blogId != null) {
            loadBlogDetail(blogId)
        } else {
            AppUtils.showToast(this, "Blog not found")
            finish()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button
        binding.fabBack.setOnClickListener {
            finish()
        }

        // Like button
        binding.layoutLike.setOnClickListener {
            currentBlog?.let { blog -> handleLike(blog) }
        }

        // Save button
        binding.layoutSave.setOnClickListener {
            currentBlog?.let { blog -> handleSave(blog) }
        }

        // Share button
        binding.layoutShare.setOnClickListener {
            currentBlog?.let { blog -> handleShare(blog) }
        }
    }

    /**
     * Load blog detail dari Firestore
     */
    private fun loadBlogDetail(blogId: String) {
        showLoading(true)

        firestore.collection(Constants.COLLECTION_BLOGS)
            .document(blogId)
            .get()
            .addOnSuccessListener { document ->
                showLoading(false)

                if (document.exists()) {
                    val blog = document.toObject(Blog::class.java)
                    blog?.let {
                        currentBlog = it
                        displayBlog(it)
                    }
                } else {
                    AppUtils.showToast(this, "Blog not found")
                    finish()
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
                finish()
            }
    }

    /**
     * Display blog data ke UI
     */
    private fun displayBlog(blog: Blog) {
        binding.apply {
            tvTitle.text = blog.title
            tvContent.text = blog.content
            tvAuthorName.text = blog.authorName
            tvTimestamp.text = blog.getFormattedDate()
            tvLikeCount.text = AppUtils.formatNumber(blog.likeCount)

            // Load image
            Glide.with(this@BlogDetailActivity)
                .load(blog.imageUrl)
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.ic_image)
                .centerCrop()
                .into(ivBlogImage)

            // Load author image (default)
            Glide.with(this@BlogDetailActivity)
                .load(R.drawable.ic_profile)
                .into(ivAuthorImage)
        }
    }

    /**
     * Handle like button
     */
    private fun handleLike(blog: Blog) {
        val blogRef = firestore.collection(Constants.COLLECTION_BLOGS).document(blog.blogId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(blogRef)
            val currentLikes = snapshot.getLong("likeCount") ?: 0
            transaction.update(blogRef, "likeCount", currentLikes + 1)
            currentLikes + 1
        }.addOnSuccessListener { newCount ->
            binding.tvLikeCount.text = AppUtils.formatNumber(newCount.toInt())
            AppUtils.showToast(this, "Liked!")
        }.addOnFailureListener { e ->
            AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
        }
    }

    /**
     * Handle save button
     */
    private fun handleSave(blog: Blog) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val savedBlogs = snapshot.get("savedBlogs") as? MutableList<String> ?: mutableListOf()

            if (savedBlogs.contains(blog.blogId)) {
                savedBlogs.remove(blog.blogId)
                transaction.update(userRef, "savedBlogs", savedBlogs)
                "removed"
            } else {
                savedBlogs.add(blog.blogId)
                transaction.update(userRef, "savedBlogs", savedBlogs)
                "added"
            }
        }.addOnSuccessListener { result ->
            val message = if (result == "added") "Saved!" else "Removed from saved"
            AppUtils.showToast(this, message)
        }.addOnFailureListener { e ->
            AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
        }
    }

    /**
     * Handle share button
     */
    private fun handleShare(blog: Blog) {
        val shareText = "${blog.title}\n\n${blog.content}\n\nShared via Blog App"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    /**
     * Show/hide loading
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
