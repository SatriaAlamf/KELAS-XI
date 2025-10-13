package com.example.mediasocial.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediasocial.R
import com.example.mediasocial.databinding.ItemNotificationBinding
import com.example.mediasocial.models.Notification
import com.example.mediasocial.models.NotificationType
import com.example.mediasocial.utils.DateUtils
import com.example.mediasocial.utils.ImageLoader

class NotificationsAdapter(
    private val notifications: MutableList<Notification>,
    private val onNotificationClick: (Notification, Int) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification, position: Int) {
            binding.apply {
                // User profile
                ImageLoader.loadProfileImage(ivUserProfile, notification.fromUserProfileImage)

                // Message
                val message = when (notification.type) {
                    NotificationType.LIKE -> "${notification.fromUsername} menyukai postingan Anda"
                    NotificationType.COMMENT -> "${notification.fromUsername} berkomentar di postingan Anda"
                    NotificationType.FOLLOW -> "${notification.fromUsername} mulai mengikuti Anda"
                    NotificationType.STORY -> "${notification.fromUsername} menambahkan story baru"
                    NotificationType.MENTION -> "${notification.fromUsername} menyebutkan Anda"
                }
                tvMessage.text = message

                // Timestamp
                tvTimestamp.text = DateUtils.getTimeAgo(notification.timestamp)

                // Post thumbnail (if applicable)
                if (notification.postImageUrl.isNotEmpty()) {
                    ivPostThumbnail.visibility = View.VISIBLE
                    ImageLoader.loadImage(ivPostThumbnail, notification.postImageUrl)
                } else {
                    ivPostThumbnail.visibility = View.GONE
                }

                // Background color based on read status
                val backgroundColor = if (notification.isRead) {
                    ContextCompat.getColor(binding.root.context, R.color.white)
                } else {
                    ContextCompat.getColor(binding.root.context, R.color.notification_unread)
                }
                cardNotification.setCardBackgroundColor(backgroundColor)

                // Click listener
                root.setOnClickListener {
                    onNotificationClick(notification, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position], position)
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotification(position: Int, notification: Notification) {
        notifications[position] = notification
        notifyItemChanged(position)
    }
}
