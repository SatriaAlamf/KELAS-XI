package com.jetpackcompose.foodorderingadmin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jetpackcompose.foodorderingadmin.MainActivity
import com.jetpackcompose.foodorderingadmin.auth.AuthManager
import com.jetpackcompose.foodorderingadmin.databinding.ActivityLoginBinding
import com.jetpackcompose.foodorderingadmin.utils.SharedPrefsManager
import com.jetpackcompose.foodorderingadmin.utils.ValidationUtils
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authManager = AuthManager()
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        // Check if already logged in
        if (prefsManager.isLoggedIn() && authManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            validateAndLogin()
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun validateAndLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Reset errors
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // Validate inputs
        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.error = "Please enter a valid email"
            return
        }

        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            return
        }

        login(email, password)
    }

    private fun login(email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.login(email, password)

            showLoading(false)

            result.onSuccess { user ->
                // Save user data
                prefsManager.saveUserData(
                    userId = user.uid,
                    email = user.email ?: "",
                    name = user.displayName ?: email.substringBefore("@")
                )

                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            }.onFailure { exception ->
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val email = binding.etEmail.text.toString().trim()

        if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.error = "Please enter your email first"
            return
        }

        lifecycleScope.launch {
            val result = authManager.resetPassword(email)

            result.onSuccess {
                Toast.makeText(
                    this@LoginActivity,
                    "Password reset email sent to $email",
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure { exception ->
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to send reset email: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.etEmail.isEnabled = !show
        binding.etPassword.isEnabled = !show
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
