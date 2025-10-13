package com.example.whatsapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.whatsapp.databinding.ActivitySplashBinding
import com.example.whatsapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check authentication status after a short delay
        lifecycleScope.launch {
            delay(2000) // Show splash for 2 seconds
            checkAuthenticationStatus()
        }
    }

    private fun checkAuthenticationStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is signed in, navigate to main activity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not signed in, navigate to login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}