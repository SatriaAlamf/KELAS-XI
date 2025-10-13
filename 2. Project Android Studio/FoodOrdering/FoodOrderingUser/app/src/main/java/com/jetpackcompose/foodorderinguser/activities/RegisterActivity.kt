package com.jetpackcompose.foodorderinguser.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jetpackcompose.foodorderinguser.MainActivity
import com.jetpackcompose.foodorderinguser.databinding.ActivityRegisterBinding
import com.jetpackcompose.foodorderinguser.auth.AuthManager
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import com.jetpackcompose.foodorderinguser.utils.ValidationUtils
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager
    private lateinit var sharedPrefsManager: SharedPrefsManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authManager = AuthManager()
        sharedPrefsManager = SharedPrefsManager(this)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (validateInput(name, email, phone, password, confirmPassword)) {
                registerUser(name, email, phone, password)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun validateInput(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (!ValidationUtils.isValidName(name)) {
            binding.etName.error = "Please enter a valid name"
            return false
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = "Please enter a valid email address"
            return false
        }
        
        if (!ValidationUtils.isValidPhone(phone)) {
            binding.etPhone.error = "Please enter a valid phone number"
            return false
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            binding.etPassword.error = "Password should be at least 6 characters"
            return false
        }
        
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }
        
        return true
    }
    
    private fun registerUser(name: String, email: String, phone: String, password: String) {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Creating Account..."
        
        lifecycleScope.launch {
            authManager.registerWithEmail(name, email, password, phone)
                .onSuccess { user ->
                    sharedPrefsManager.saveUserData(user)
                    Toast.makeText(this@RegisterActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                .onFailure { error ->
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Create Account"
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}