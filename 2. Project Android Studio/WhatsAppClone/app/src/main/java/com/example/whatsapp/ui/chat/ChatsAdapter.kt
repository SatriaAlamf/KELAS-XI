package com.example.whatsapp.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.data.model.Conversation
import com.example.whatsapp.data.model.ConversationType
import com.example.whatsapp.databinding.ItemChatBinding
import com.example.whatsapp.utils.Extensions.loadCircularImage
import com.example.whatsapp.utils.Extensions.toTimeString

class ChatsAdapter(
    private val onItemClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ChatsAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(
        private val binding: ItemChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.apply {
                // Set chat name
                tvChatName.text = when (conversation.type) {
                    ConversationType.PRIVATE -> {
                        // For private chats, show the other participant's name
                        conversation.participantNames.values.firstOrNull() ?: "Unknown"
                    }
                    ConversationType.GROUP -> {
                        conversation.name ?: "Group Chat"
                    }
                    ConversationType.COMMUNITY_CHANNEL -> {
                        conversation.name ?: "Community Channel"
                    }
                }

                // Set last message
                conversation.lastMessage?.let { lastMessage ->
                    tvLastMessage.text = if (lastMessage.isDeleted) {
                        "This message was deleted"
                    } else {
                        when {
                            lastMessage.text.isNotEmpty() -> lastMessage.text
                            else -> "Media" // For media messages
                        }
                    }
                    
                    // Set time
                    tvTime.text = lastMessage.timestamp.toTimeString()
                } ?: run {
                    tvLastMessage.text = "No messages yet"
                    tvTime.text = ""
                }

                // Set profile image (placeholder for now)
                // In a real implementation, you would load the actual image
                // ivProfile.loadCircularImage(profileImageUrl)

                // Set unread count
                // This is simplified - in a real app you'd get the current user ID from auth
                val currentUserId = "current_user_id" // TODO: Get from auth
                val unreadCount = conversation.unreadCounts[currentUserId] ?: 0
                if (unreadCount > 0) {
                    unreadBadge.visibility = android.view.View.VISIBLE
                    unreadBadge.text = if (unreadCount > 99) "99+" else unreadCount.toString()
                } else {
                    unreadBadge.visibility = android.view.View.GONE
                }

                // Set click listener
                root.setOnClickListener {
                    onItemClick(conversation)
                }
            }
        }
    }

    private class ChatDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}