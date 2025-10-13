package com.example.mediasocial.models

data class Post(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImage: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: List<String> = emptyList(), // List user IDs yang like
    val likesCount: Int = 0,
    val commentsCount: Int = 0
) {
    constructor() : this("", "", "", "", "", "", 0L, emptyList(), 0, 0)
    
    fun isLikedBy(userId: String): Boolean {
        return likes.contains(userId)
    }
}
