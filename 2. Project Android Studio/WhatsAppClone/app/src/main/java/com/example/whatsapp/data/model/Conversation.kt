package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val id: String = "",
    val type: ConversationType = ConversationType.PRIVATE,
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val lastMessage: LastMessage? = null,
    val lastUpdated: Date = Date(),
    val unreadCounts: Map<String, Int> = emptyMap(),
    
    // Group specific fields
    val name: String? = null,
    val description: String? = null,
    val groupImageUrl: String? = null,
    val adminIds: List<String> = emptyList(),
    val createdBy: String? = null,
    val createdAt: Date = Date(),
    
    // Community specific fields
    val communityId: String? = null,
    val isAnnouncement: Boolean = false
)

data class LastMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val timestamp: Date = Date(),
    val isDeleted: Boolean = false
)

enum class ConversationType {
    PRIVATE, GROUP, COMMUNITY_CHANNEL
}