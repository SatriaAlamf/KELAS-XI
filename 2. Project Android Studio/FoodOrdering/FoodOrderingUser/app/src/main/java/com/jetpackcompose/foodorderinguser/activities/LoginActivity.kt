package com.jetpackcompose.foodorderinguser.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jetpackcompose.foodorderinguser.MainActivity
import com.jetpackcompose.foodorderinguser.R
import com.jetpackcompose.foodorderinguser.auth.AuthManager
import com.jetpackcompose.foodorderinguser.databinding.ActivityLoginBinding
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import com.jetpackcompose.foodorderinguser.utils.ValidationUtils
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager
    private lateinit var sharedPrefsManager: SharedPrefsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager()
        sharedPrefsManager = SharedPrefsManager(this)
        
        // Check if user is already logged in
        if (sharedPrefsManager.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }
        
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (ValidationUtils.isValidEmail(email)) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = "Please enter a valid email address"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        
        return true
    }
    
    private fun loginUser(email: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Logging in..."
        
        lifecycleScope.launch {
            authManager.loginWithEmail(email, password)
                .onSuccess { user ->
                    sharedPrefsManager.saveUserData(user)
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                .onFailure { error ->
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Login"
                    Toast.makeText(this@LoginActivity, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    
    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            authManager.resetPassword(email)
                .onSuccess {
                    Toast.makeText(this@LoginActivity, "Password reset email sent!", Toast.LENGTH_LONG).show()
                }
                .onFailure { error ->
                    Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}