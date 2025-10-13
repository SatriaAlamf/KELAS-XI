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
import com.google.firebase.firestore.Query

/**
 * My Blogs Activity
 * Menampilkan daftar blog yang dibuat oleh user
 */
class MyBlogsActivity : AppCompatActivity() {

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
        loadMyBlogs()
    }

    private fun setupToolbar() {
        binding.tvToolbarTitle.text = "My Blogs"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(
            onLikeClick = { blog -> handleLike(blog) },
            onSaveClick = { blog -> handleSave(blog) },
            onShareClick = { blog -> handleShare(blog) },
            onBlogClick = { blog -> openBlogDetail(blog) }
        )

        binding.rvBlogs.apply {
            layoutManager = LinearLayoutManager(this@MyBlogsActivity)
            adapter = blogAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * Load blogs created by current user
     */
    private fun loadMyBlogs() {
        showLoading(true)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            showLoading(false)
            return
        }

        firestore.collection(Constants.COLLECTION_BLOGS)
            .whereEqualTo("authorId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                showLoading(false)

                if (error != null) {
                    AppUtils.showToast(this, error.message ?: Constants.ERROR_GENERIC)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val blogs = snapshot.toObjects(Blog::class.java)
                    blogAdapter.submitList(blogs)
                    showEmptyState(false)
                } else {
                    blogAdapter.submitList(emptyList())
                    binding.tvEmptyMessage.text = "You haven't created any blogs yet"
                    showEmptyState(true)
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
