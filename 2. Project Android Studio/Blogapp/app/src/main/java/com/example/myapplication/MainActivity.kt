package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.activities.AddBlogActivity
import com.example.myapplication.activities.BlogDetailActivity
import com.example.myapplication.activities.ProfileActivity
import com.example.myapplication.adapters.BlogAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.models.Blog
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Main Activity - Home Screen
 * Menampilkan daftar semua blog dengan RecyclerView
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var blogAdapter: BlogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupBottomNavigation()
        loadBlogs()
    }

    /**
     * Setup RecyclerView dengan adapter
     */
    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter(
            onLikeClick = { blog -> handleLike(blog) },
            onSaveClick = { blog -> handleSave(blog) },
            onShareClick = { blog -> handleShare(blog) },
            onBlogClick = { blog -> openBlogDetail(blog) }
        )

        binding.rvBlogs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = blogAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * Setup Bottom Navigation
     */
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home, refresh
                    loadBlogs()
                    true
                }
                R.id.nav_add -> {
                    // Navigate to Add Blog Activity
                    val intent = Intent(this, AddBlogActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Navigate to Profile Activity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Load semua blog dari Firestore
     */
    private fun loadBlogs() {
        showLoading(true)

        firestore.collection(Constants.COLLECTION_BLOGS)
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
                    showEmptyState(true)
                }
            }
    }

    /**
     * Handle like button click
     */
    private fun handleLike(blog: Blog) {
        // Increment like count dengan Transaction untuk avoid race condition
        val blogRef = firestore.collection(Constants.COLLECTION_BLOGS).document(blog.blogId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(blogRef)
            val currentLikes = snapshot.getLong("likeCount") ?: 0
            transaction.update(blogRef, "likeCount", currentLikes + 1)
        }.addOnSuccessListener {
            AppUtils.showToast(this, "Liked!")
        }.addOnFailureListener { e ->
            AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
        }
    }

    /**
     * Handle save button click
     */
    private fun handleSave(blog: Blog) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)

        // Toggle save status
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
     * Handle share button click
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
     * Open blog detail screen
     */
    private fun openBlogDetail(blog: Blog) {
        val intent = Intent(this, BlogDetailActivity::class.java)
        intent.putExtra(Constants.EXTRA_BLOG_ID, blog.blogId)
        startActivity(intent)
    }

    /**
     * Show/hide loading progress
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    /**
     * Show/hide empty state
     */
    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvBlogs.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Set selected item to home
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }
}