package com.example.mediasocial.models

data class Notification(
    val notificationId: String = "",
    val userId: String = "", // User yang menerima notifikasi
    val fromUserId: String = "", // User yang membuat notifikasi
    val fromUsername: String = "",
    val fromUserProfileImage: String = "",
    val type: NotificationType = NotificationType.LIKE,
    val postId: String = "", // ID post terkait (jika ada)
    val postImageUrl: String = "", // Thumbnail post
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    constructor() : this("", "", "", "", "", NotificationType.LIKE, "", "", "", 0L, false)
}

enum class NotificationType {
    LIKE,           // Seseorang menyukai postingan
    COMMENT,        // Seseorang berkomentar
    FOLLOW,         // Seseorang mengikuti
    STORY,          // Seseorang menambahkan story baru
    MENTION         // Seseorang mention
}
