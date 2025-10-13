package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.utils.AppUtils
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth

/**
 * Login Activity
 * Handles user authentication dengan Firebase Auth
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Login Button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        // Navigate to Register
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                AppUtils.showToast(this, "Please enter your email first")
                return@setOnClickListener
            }

            resetPassword(email)
        }
    }

    /**
     * Validasi input email dan password
     */
    private fun validateInput(email: String, password: String): Boolean {
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

        // Check internet connection
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showToast(this, Constants.ERROR_NETWORK)
            return false
        }

        return true
    }

    /**
     * Login user dengan Firebase Auth
     */
    private fun loginUser(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    // Login berhasil
                    AppUtils.showToast(this, Constants.SUCCESS_LOGIN)
                    navigateToMain()
                } else {
                    // Login gagal
                    val errorMessage = task.exception?.message ?: Constants.ERROR_AUTH_FAILED
                    AppUtils.showToast(this, errorMessage)
                }
            }
    }

    /**
     * Reset password melalui email
     */
    private fun resetPassword(email: String) {
        showLoading(true)

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    AppUtils.showToast(this, "Password reset email sent!")
                } else {
                    val errorMessage = task.exception?.message ?: Constants.ERROR_GENERIC
                    AppUtils.showToast(this, errorMessage)
                }
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
        binding.btnLogin.isEnabled = !isLoading
    }
}
