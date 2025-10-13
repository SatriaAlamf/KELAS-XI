package com.example.whatsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val about: String = "Hey there! I am using WhatsApp.",
    val lastSeen: Date = Date(),
    val isOnline: Boolean = false,
    val fcmTokens: List<String> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class UserSettings(
    val lastSeenVisibility: LastSeenVisibility = LastSeenVisibility.EVERYONE,
    val profilePhotoVisibility: ProfileVisibility = ProfileVisibility.EVERYONE,
    val aboutVisibility: AboutVisibility = AboutVisibility.EVERYONE,
    val readReceipts: Boolean = true,
    val groupsVisibility: GroupsVisibility = GroupsVisibility.EVERYONE
)

enum class LastSeenVisibility {
    EVERYONE, CONTACTS, NOBODY
}

enum class ProfileVisibility {
    EVERYONE, CONTACTS, NOBODY
}

enum class AboutVisibility {
    EVERYONE, CONTACTS, NOBODY
}

enum class GroupsVisibility {
    EVERYONE, CONTACTS, NOBODY
}