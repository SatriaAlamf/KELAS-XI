package com.example.mediasocial.models

data class Story(
    val storyId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 jam
    val viewedBy: List<String> = emptyList() // List user IDs yang sudah melihat
) {
    constructor() : this("", "", "", "", "", 0L, 0L, emptyList())
    
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }
    
    fun isViewedBy(userId: String): Boolean {
        return viewedBy.contains(userId)
    }
}
