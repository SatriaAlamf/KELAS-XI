package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaThumbnailUrl: String? = null,
    val mediaSize: Long = 0,
    val mediaDuration: Long = 0, // For audio/video in milliseconds
    val timestamp: Date = Date(),
    val status: MessageStatus = MessageStatus.SENT,
    val readBy: Map<String, Date> = emptyMap(),
    val deliveredTo: Map<String, Date> = emptyMap(),
    val replyTo: String? = null,
    val replyToMessage: Message? = null,
    val isForwarded: Boolean = false,
    val isStarred: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedFor: List<String> = emptyList(), // UIDs who deleted this message
    val reactions: Map<String, String> = emptyMap(), // userId to emoji
    
    // Location message fields
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    
    // Contact message fields
    val contactName: String? = null,
    val contactPhone: String? = null
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, LOCATION, CONTACT, STICKER, GIF
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, FAILED
}