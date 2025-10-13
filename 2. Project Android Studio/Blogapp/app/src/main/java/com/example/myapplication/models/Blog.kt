package com.example.myapplication.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Data Model untuk Blog
 * 
 * Properties:
 * - blogId: ID unik dari Firestore document
 * - title: Judul blog
 * - content: Konten/isi blog
 * - authorId: User ID pembuat blog
 * - authorName: Nama pembuat blog
 * - authorEmail: Email pembuat blog
 * - timestamp: Waktu pembuatan blog (auto-generated oleh Firestore)
 * - imageUrl: URL gambar yang diupload ke Firebase Storage
 * - likeCount: Jumlah like pada blog
 */
data class Blog(
    @DocumentId
    var blogId: String = "",
    var title: String = "",
    var content: String = "",
    var authorId: String = "",
    var authorName: String = "",
    var authorEmail: String = "",
    @ServerTimestamp
    var timestamp: Timestamp? = null,
    var imageUrl: String = "",
    var likeCount: Int = 0
) {
    /**
     * Fungsi helper untuk convert ke Map (untuk save ke Firestore)
     */
    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "blogId" to blogId,
            "title" to title,
            "content" to content,
            "authorId" to authorId,
            "authorName" to authorName,
            "authorEmail" to authorEmail,
            "timestamp" to timestamp,
            "imageUrl" to imageUrl,
            "likeCount" to likeCount
        )
    }
    
    /**
     * Fungsi untuk mendapatkan tanggal yang sudah diformat
     */
    fun getFormattedDate(): String {
        if (timestamp == null) return "Just now"
        
        val now = System.currentTimeMillis()
        val diff = now - timestamp!!.toDate().time
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }
}
