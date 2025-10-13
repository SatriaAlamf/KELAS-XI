package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "communities")
data class Community(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val adminIds: List<String> = emptyList(),
    val memberCount: Int = 0,
    val isPublic: Boolean = true,
    val inviteCode: String? = null,
    val channels: List<CommunityChannel> = emptyList(),
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class CommunityChannel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: ChannelType = ChannelType.GENERAL,
    val isAnnouncement: Boolean = false,
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val createdAt: Date = Date()
)

enum class ChannelType {
    GENERAL, ANNOUNCEMENT
}