package com.example.whatsapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.whatsapp.databinding.ActivityEmailSigninBinding
import com.example.whatsapp.ui.main.MainActivity
import com.example.whatsapp.utils.Extensions.isValidEmail
import com.example.whatsapp.viewmodel.AuthViewModel
import com.example.whatsapp.viewmodel.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailSigninBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            signInWithEmail()
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, EmailSignUpActivity::class.java))
        }
    }

    private fun signInWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validation
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!email.isValidEmail()) {
            binding.tilEmail.error = "Invalid email format"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (isValid) {
            viewModel.signInWithEmailPassword(email, password)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSignIn.isEnabled = false
                    }
                    is AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        navigateToMain()
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSignIn.isEnabled = true
                        Toast.makeText(this@EmailSignInActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSignIn.isEnabled = true
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}