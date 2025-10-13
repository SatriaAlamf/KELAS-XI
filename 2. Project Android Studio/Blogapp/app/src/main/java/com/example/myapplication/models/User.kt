package com.example.myapplication.models

import com.google.firebase.firestore.DocumentId

/**
 * Data Model untuk User
 * 
 * Properties:
 * - userId: ID unik user dari Firebase Auth
 * - name: Nama lengkap user
 * - email: Email user
 * - profileImageUrl: URL foto profil (opsional)
 * - savedBlogs: List ID blog yang di-bookmark oleh user
 */
data class User(
    @DocumentId
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var profileImageUrl: String = "",
    var savedBlogs: MutableList<String> = mutableListOf()
) {
    /**
     * Fungsi helper untuk convert ke Map (untuk save ke Firestore)
     */
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "profileImageUrl" to profileImageUrl,
            "savedBlogs" to savedBlogs
        )
    }
    
    /**
     * Fungsi untuk check apakah blog sudah disimpan
     */
    fun isBlogSaved(blogId: String): Boolean {
        return savedBlogs.contains(blogId)
    }
    
    /**
     * Fungsi untuk toggle saved blog
     */
    fun toggleSaveBlog(blogId: String) {
        if (isBlogSaved(blogId)) {
            savedBlogs.remove(blogId)
        } else {
            savedBlogs.add(blogId)
        }
    }
}
