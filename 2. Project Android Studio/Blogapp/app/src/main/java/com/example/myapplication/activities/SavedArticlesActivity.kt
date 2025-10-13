package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.BlogAdapter
import com.example.myapplication.databinding.ActivitySavedArticlesBinding
import com.example.myapplication.models.Blog
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Saved Articles Activity
 * Menampilkan daftar artikel yang disimpan oleh user
 */
class SavedArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedArticlesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupToolbar()
        setupRecyclerView()
        loadSavedArticles()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(
            onLikeClick = { blog -> handleLike(blog) },
            onSaveClick = { blog -> handleUnsave(blog) },
            onShareClick = { blog -> handleShare(blog) },
            onBlogClick = { blog -> openBlogDetail(blog) }
        )

        binding.rvBlogs.apply {
            layoutManager = LinearLayoutManager(this@SavedArticlesActivity)
            adapter = blogAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * Load saved articles
     */
    private fun loadSavedArticles() {
        showLoading(true)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            showLoading(false)
            return
        }

        // Get saved blog IDs dari user document
        firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val savedBlogIds = document.get("savedBlogs") as? List<String> ?: emptyList()

                if (savedBlogIds.isEmpty()) {
                    showLoading(false)
                    showEmptyState(true)
                    return@addOnSuccessListener
                }

                // Load blogs berdasarkan saved IDs
                loadBlogsByIds(savedBlogIds)
            }
            .addOnFailureListener { e ->
                showLoading(false)
                AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
            }
    }

    /**
     * Load blogs by IDs
     */
    private fun loadBlogsByIds(blogIds: List<String>) {
        val blogs = mutableListOf<Blog>()
        var loadedCount = 0

        for (blogId in blogIds) {
            firestore.collection(Constants.COLLECTION_BLOGS)
                .document(blogId)
                .get()
                .addOnSuccessListener { document ->
                    loadedCount++
                    
                    if (document.exists()) {
                        document.toObject(Blog::class.java)?.let { blogs.add(it) }
                    }

                    // Jika semua blog sudah di-load
                    if (loadedCount == blogIds.size) {
                        showLoading(false)
                        if (blogs.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            blogAdapter.submitList(blogs)
                            showEmptyState(false)
                        }
                    }
                }
                .addOnFailureListener {
                    loadedCount++
                    if (loadedCount == blogIds.size) {
                        showLoading(false)
                        blogAdapter.submitList(blogs)
                        showEmptyState(blogs.isEmpty())
                    }
                }
        }
    }

    private fun handleLike(blog: Blog) {
        val blogRef = firestore.collection(Constants.COLLECTION_BLOGS).document(blog.blogId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(blogRef)
            val currentLikes = snapshot.getLong("likeCount") ?: 0
            transaction.update(blogRef, "likeCount", currentLikes + 1)
        }.addOnSuccessListener {
            AppUtils.showToast(this, "Liked!")
        }
    }

    private fun handleUnsave(blog: Blog) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val savedBlogs = snapshot.get("savedBlogs") as? MutableList<String> ?: mutableListOf()
            savedBlogs.remove(blog.blogId)
            transaction.update(userRef, "savedBlogs", savedBlogs)
        }.addOnSuccessListener {
            AppUtils.showToast(this, "Removed from saved")
            loadSavedArticles() // Refresh list
        }
    }

    private fun handleShare(blog: Blog) {
        val shareText = "${blog.title}\n\n${blog.content}\n\nShared via Blog App"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun openBlogDetail(blog: Blog) {
        val intent = Intent(this, BlogDetailActivity::class.java)
        intent.putExtra(Constants.EXTRA_BLOG_ID, blog.blogId)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvBlogs.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}
