package com.example.mediasocial.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediasocial.adapters.NotificationsAdapter
import com.example.mediasocial.databinding.FragmentNotificationsBinding
import com.example.mediasocial.models.Notification
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private val notifications = mutableListOf<Notification>()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        setupRecyclerView()
        loadNotifications()
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter(notifications) { notification, position ->
            markAsRead(notification, position)
        }
        
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationsAdapter
        }
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE
        
        firestore.collection(Constants.COLLECTION_NOTIFICATIONS)
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                notifications.clear()
                
                for (document in documents) {
                    val notification = document.toObject(Notification::class.java)
                    notifications.add(notification)
                }
                
                notificationsAdapter.notifyDataSetChanged()
                
                if (notifications.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvNotifications.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvNotifications.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                ToastUtils.showShort(requireContext(), "Gagal memuat notifikasi: ${exception.message}")
            }
    }

    private fun markAsRead(notification: Notification, position: Int) {
        if (notification.isRead) return
        
        firestore.collection(Constants.COLLECTION_NOTIFICATIONS)
            .document(notification.notificationId)
            .update("isRead", true)
            .addOnSuccessListener {
                val updatedNotification = notification.copy(isRead = true)
                notificationsAdapter.updateNotification(position, updatedNotification)
            }
            .addOnFailureListener { exception ->
                ToastUtils.showShort(requireContext(), "Gagal update notifikasi: ${exception.message}")
            }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
