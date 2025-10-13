package com.example.mediasocial.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // Firestore memerlukan constructor tanpa parameter
    constructor() : this("", "", "", "", "", 0L)
}
