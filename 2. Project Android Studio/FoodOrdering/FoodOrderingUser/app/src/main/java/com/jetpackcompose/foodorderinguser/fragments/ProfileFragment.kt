package com.jetpackcompose.foodorderinguser.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jetpackcompose.foodorderinguser.activities.LoginActivity
import com.jetpackcompose.foodorderinguser.activities.ProfileEditActivity
import com.jetpackcompose.foodorderinguser.auth.AuthManager
import com.jetpackcompose.foodorderinguser.databinding.FragmentProfileBinding
import com.jetpackcompose.foodorderinguser.models.User
import com.jetpackcompose.foodorderinguser.repository.UserRepository
import com.jetpackcompose.foodorderinguser.utils.ImageLoader
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authManager: AuthManager
    private lateinit var userRepository: UserRepository
    private lateinit var sharedPrefsManager: SharedPrefsManager
    
    private var currentUser: User? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authManager = AuthManager()
        userRepository = UserRepository()
        sharedPrefsManager = SharedPrefsManager(requireContext())
        
        setupClickListeners()
        loadUserProfile()
    }
    
    private fun setupClickListeners() {
        binding.cardEditProfile.setOnClickListener {
            currentUser?.let { user ->
                val intent = Intent(requireContext(), ProfileEditActivity::class.java)
                startActivity(intent)
            }
        }
        
        binding.cardAddresses.setOnClickListener {
            Toast.makeText(requireContext(), "Address management coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardPaymentMethods.setOnClickListener {
            Toast.makeText(requireContext(), "Payment methods coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardNotifications.setOnClickListener {
            Toast.makeText(requireContext(), "Notification settings coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardSupport.setOnClickListener {
            Toast.makeText(requireContext(), "Support center coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.cardAbout.setOnClickListener {
            Toast.makeText(requireContext(), "About us coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun loadUserProfile() {
        val userId = sharedPrefsManager.getUserId()
        if (userId.isEmpty()) {
            redirectToLogin()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            userRepository.getUser(userId)
                .onSuccess { user ->
                    binding.progressBar.visibility = View.GONE
                    if (user != null) {
                        currentUser = user
                        updateUI(user)
                    } else {
                        Toast.makeText(requireContext(), "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error loading profile: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun updateUI(user: User) {
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        binding.tvUserPhone.text = user.phone.ifEmpty { "No phone number" }
        
        // Load profile image
        ImageLoader.loadCircularImage(
            requireContext(),
            binding.ivProfileImage,
            user.profileImageUrl
        )
        
        // Update address count
        binding.tvAddressCount.text = "${user.addresses.size} saved"
    }
    
    private fun logout() {
        lifecycleScope.launch {
            authManager.logout()
            sharedPrefsManager.logout()
            
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }
    }
    
    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    override fun onResume() {
        super.onResume()
        loadUserProfile() // Refresh profile when fragment becomes visible
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}