package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "status")
data class Status(
    @PrimaryKey
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerAvatarUrl: String = "",
    val mediaUrl: String = "",
    val thumbnailUrl: String? = null,
    val type: StatusType = StatusType.IMAGE,
    val text: String? = null,
    val backgroundColor: String? = null,
    val createdAt: Date = Date(),
    val expiresAt: Date = Date(),
    val viewers: List<StatusViewer> = emptyList(),
    val isViewed: Boolean = false,
    val viewedAt: Date? = null
)

data class StatusViewer(
    val userId: String = "",
    val userName: String = "",
    val viewedAt: Date = Date()
)

enum class StatusType {
    IMAGE, VIDEO, TEXT
}