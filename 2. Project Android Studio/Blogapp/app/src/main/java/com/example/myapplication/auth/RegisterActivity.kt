package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.models.User
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Register Activity
 * Handles user registration dengan Firebase Auth & Firestore
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Register Button
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }

        // Navigate to Login
        binding.tvLogin.setOnClickListener {
            finish() // Kembali ke Login atau Welcome screen
        }
    }

    /**
     * Validasi input registration
     */
    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Check empty name
        if (name.isEmpty()) {
            binding.etName.error = Constants.ERROR_INVALID_NAME
            binding.etName.requestFocus()
            return false
        }

        // Check name length
        if (!AppUtils.isValidName(name)) {
            binding.etName.error = Constants.ERROR_INVALID_NAME
            binding.etName.requestFocus()
            return false
        }

        // Check empty email
        if (email.isEmpty()) {
            binding.etEmail.error = Constants.ERROR_INVALID_EMAIL
            binding.etEmail.requestFocus()
            return false
        }

        // Check valid email format
        if (!AppUtils.isValidEmail(email)) {
            binding.etEmail.error = Constants.ERROR_INVALID_EMAIL
            binding.etEmail.requestFocus()
            return false
        }

        // Check empty password
        if (password.isEmpty()) {
            binding.etPassword.error = Constants.ERROR_INVALID_PASSWORD
            binding.etPassword.requestFocus()
            return false
        }

        // Check password length
        if (!AppUtils.isValidPassword(password)) {
            binding.etPassword.error = Constants.ERROR_INVALID_PASSWORD
            binding.etPassword.requestFocus()
            return false
        }

        // Check password match
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            binding.etConfirmPassword.requestFocus()
            return false
        }

        // Check internet connection
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showToast(this, Constants.ERROR_NETWORK)
            return false
        }

        return true
    }

    /**
     * Register user dengan Firebase Auth
     */
    private fun registerUser(name: String, email: String, password: String) {
        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration berhasil, update profile dan save ke Firestore
                    val user = auth.currentUser
                    user?.let {
                        // Update display name di Firebase Auth
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    // Save user data ke Firestore
                                    saveUserToFirestore(it.uid, name, email)
                                } else {
                                    showLoading(false)
                                    AppUtils.showToast(
                                        this,
                                        profileTask.exception?.message ?: Constants.ERROR_GENERIC
                                    )
                                }
                            }
                    }
                } else {
                    showLoading(false)
                    val errorMessage = task.exception?.message ?: Constants.ERROR_AUTH_FAILED
                    AppUtils.showToast(this, errorMessage)
                }
            }
    }

    /**
     * Save user data ke Firestore
     */
    private fun saveUserToFirestore(userId: String, name: String, email: String) {
        val user = User(
            userId = userId,
            name = name,
            email = email,
            profileImageUrl = "",
            savedBlogs = mutableListOf()
        )

        firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                showLoading(false)
                AppUtils.showToast(this, Constants.SUCCESS_REGISTER)
                navigateToMain()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                AppUtils.showToast(this, e.message ?: Constants.ERROR_GENERIC)
            }
    }

    /**
     * Navigate ke MainActivity
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Show/hide loading progress
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}
