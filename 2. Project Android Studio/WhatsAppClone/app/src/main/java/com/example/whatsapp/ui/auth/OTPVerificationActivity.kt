package com.example.whatsapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.whatsapp.databinding.ActivityOtpVerificationBinding
import com.example.whatsapp.ui.main.MainActivity
import com.example.whatsapp.viewmodel.AuthViewModel
import com.example.whatsapp.viewmodel.AuthState
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private val viewModel: AuthViewModel by viewModels()
    private var verificationId: String? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificationId = intent.getStringExtra("verification_id")
        phoneNumber = intent.getStringExtra("phone_number")

        binding.tvPhoneNumber.text = phoneNumber

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnVerify.setOnClickListener {
            verifyOTP()
        }

        binding.tvResendCode.setOnClickListener {
            // TODO: Implement resend OTP functionality
            Toast.makeText(this, "Resend functionality will be implemented", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyOTP() {
        val otp = binding.etOTP.text.toString().trim()
        val name = binding.etName.text.toString().trim()

        // Validation
        binding.tilOTP.error = null
        binding.tilName.error = null

        var isValid = true

        if (otp.isEmpty() || otp.length < 6) {
            binding.tilOTP.error = "Please enter valid 6-digit OTP"
            isValid = false
        }

        if (name.isEmpty()) {
            binding.tilName.error = "Please enter your name"
            isValid = false
        }

        if (isValid && verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            viewModel.verifyPhoneCredential(credential, name)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnVerify.isEnabled = false
                    }
                    is AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        navigateToMain()
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnVerify.isEnabled = true
                        Toast.makeText(this@OTPVerificationActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnVerify.isEnabled = true
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity() // Clear the back stack
    }
}