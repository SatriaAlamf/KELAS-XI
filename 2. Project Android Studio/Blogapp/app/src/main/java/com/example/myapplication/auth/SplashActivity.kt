package com.example.myapplication.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivitySplashBinding
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth

/**
 * Splash Screen Activity
 * Menampilkan splash screen selama 3 detik, kemudian:
 * - Jika user sudah login -> ke MainActivity
 * - Jika user belum login -> ke WelcomeActivity
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Delay 3 detik kemudian navigate
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, Constants.SPLASH_DELAY)
    }

    /**
     * Navigate ke screen berikutnya berdasarkan status login
     */
    private fun navigateToNextScreen() {
        val currentUser = auth.currentUser
        
        val intent = if (currentUser != null) {
            // User sudah login, langsung ke MainActivity
            Intent(this, MainActivity::class.java)
        } else {
            // User belum login, ke WelcomeActivity
            Intent(this, WelcomeActivity::class.java)
        }

        startActivity(intent)
        finish() // Close splash activity
    }
}
