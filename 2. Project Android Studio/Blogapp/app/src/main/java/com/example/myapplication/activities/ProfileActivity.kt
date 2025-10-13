package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.auth.WelcomeActivity
import com.example.myapplication.databinding.ActivityProfileBinding
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Profile Activity
 * Menampilkan informasi user dan menu profile
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadUserProfile()
        setupClickListeners()
    }

    /**
     * Load user profile data
     */
    private fun loadUserProfile() {
        showLoading(true)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Set basic info dari Firebase Auth
            binding.tvUserName.text = currentUser.displayName ?: "Anonymous"
            binding.tvUserEmail.text = currentUser.email ?: ""

            // Load profile image (default sementara)
            Glide.with(this)
                .load(R.drawable.ic_profile)
                .into(binding.ivProfileImage)

            // Load additional info dari Firestore (optional)
            firestore.collection(Constants.COLLECTION_USERS)
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    showLoading(false)
                    if (document.exists()) {
                        val name = document.getString("name")
                        if (!name.isNullOrEmpty()) {
                            binding.tvUserName.text = name
                        }
                    }
                }
                .addOnFailureListener {
                    showLoading(false)
                }
        } else {
            showLoading(false)
            navigateToWelcome()
        }
    }

    private fun setupClickListeners() {
        // My Blogs
        binding.cardMyBlogs.setOnClickListener {
            val intent = Intent(this, MyBlogsActivity::class.java)
            startActivity(intent)
        }

        // Saved Articles
        binding.cardSavedArticles.setOnClickListener {
            val intent = Intent(this, SavedArticlesActivity::class.java)
            startActivity(intent)
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * Show logout confirmation dialog
     */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    /**
     * Perform logout
     */
    private fun performLogout() {
        showLoading(true)
        
        auth.signOut()
        
        AppUtils.showToast(this, Constants.SUCCESS_LOGOUT)
        navigateToWelcome()
    }

    /**
     * Navigate to Welcome screen
     */
    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Show/hide loading
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
