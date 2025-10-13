package com.example.mediasocial.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mediasocial.databinding.ActivityRegisterBinding
import com.example.mediasocial.models.User
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(username, email, password)) {
                registerUser(username, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username tidak boleh kosong"
            return false
        }
        if (username.length < 3) {
            binding.tilUsername.error = "Username minimal 3 karakter"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            return false
        }
        return true
    }

    private fun registerUser(username: String, email: String, password: String) {
        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: ""
                val user = User(
                    uid = userId,
                    username = username,
                    email = email,
                    profileImageUrl = "",
                    bio = ""
                )

                // Save user data to Firestore
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        showLoading(false)
                        ToastUtils.showShort(this, "Registrasi berhasil!")
                        navigateToMain()
                    }
                    .addOnFailureListener { exception ->
                        showLoading(false)
                        ToastUtils.showLong(this, "Gagal menyimpan data: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                ToastUtils.showLong(this, "Registrasi gagal: ${exception.message}")
            }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.etUsername.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }
}
