package com.jetpackcompose.foodorderingadmin

package com.jetpackcompose.foodorderingadmin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jetpackcompose.foodorderingadmin.activities.LoginActivity
import com.jetpackcompose.foodorderingadmin.auth.AuthManager
import com.jetpackcompose.foodorderingadmin.databinding.ActivityMainBinding
import com.jetpackcompose.foodorderingadmin.utils.SharedPrefsManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authManager = AuthManager()
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        // Check if user is logged in
        if (!prefsManager.isLoggedIn() || !authManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
