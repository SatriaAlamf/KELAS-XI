package com.example.mediasocial.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediasocial.activities.LoginActivity
import com.example.mediasocial.databinding.FragmentProfileBinding
import com.example.mediasocial.models.Post
import com.example.mediasocial.models.User
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.ImageLoader
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
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
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        setupClickListeners()
        loadUserProfile()
        loadUserStats()
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            // TODO: Implement edit profile functionality
            ToastUtils.showShort(requireContext(), "Fitur edit profil akan segera hadir")
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        firestore.collection(Constants.COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                currentUser = document.toObject(User::class.java)
                currentUser?.let { user ->
                    binding.apply {
                        tvUsername.text = user.username
                        tvEmail.text = user.email
                        
                        if (user.bio.isNotEmpty()) {
                            tvBio.visibility = View.VISIBLE
                            tvBio.text = user.bio
                        } else {
                            tvBio.visibility = View.GONE
                        }
                        
                        ImageLoader.loadProfileImage(ivProfileImage, user.profileImageUrl)
                    }
                }
            }
            .addOnFailureListener { exception ->
                ToastUtils.showShort(requireContext(), "Gagal memuat profil: ${exception.message}")
            }
    }

    private fun loadUserStats() {
        val userId = auth.currentUser?.uid ?: return
        
        // Load posts count
        firestore.collection(Constants.COLLECTION_POSTS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                binding.tvPostsCount.text = documents.size().toString()
            }
        
        // Load stories count (active only)
        val currentTime = System.currentTimeMillis()
        firestore.collection(Constants.COLLECTION_STORIES)
            .whereEqualTo("userId", userId)
            .whereGreaterThan("expiresAt", currentTime)
            .get()
            .addOnSuccessListener { documents ->
                binding.tvStoriesCount.text = documents.size().toString()
            }
        
        // Load user posts
        loadUserPosts(userId)
    }

    private fun loadUserPosts(userId: String) {
        firestore.collection(Constants.COLLECTION_POSTS)
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val posts = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                
                if (posts.isEmpty()) {
                    binding.tvEmptyPosts.visibility = View.VISIBLE
                    binding.rvMyPosts.visibility = View.GONE
                } else {
                    binding.tvEmptyPosts.visibility = View.GONE
                    binding.rvMyPosts.visibility = View.VISIBLE
                    // TODO: Setup grid adapter for posts
                }
            }
            .addOnFailureListener { exception ->
                ToastUtils.showShort(requireContext(), "Gagal memuat postingan: ${exception.message}")
            }
    }

    private fun logout() {
        auth.signOut()
        
        // Navigate to login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
