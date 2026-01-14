package com.pinjaminperpus.mobile_android.data.model

/**
 * Data class representing a Book in the library
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val category: String,
    val isbn: String,
    val coverResId: Int? = null, // Resource ID for drawable
    val description: String,
    val rating: Float,
    val reviewCount: Int = 0,
    val totalStock: Int,
    val availableStock: Int,
    val pages: Int = 0,
    val language: String = "Indonesia",
    val year: Int = 2020
) {
    val isAvailable: Boolean get() = availableStock > 0
}

/**
 * Data class representing a User
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val membershipId: String,
    val avatarResId: Int? = null,
    val totalBorrowed: Int = 0,
    val currentlyBorrowing: Int = 0,
    val readingPoints: Int = 0
)

/**
 * Data class representing a Borrowing transaction
 */
data class Borrowing(
    val id: String,
    val book: Book,
    val borrowDate: String,
    val dueDate: String,
    val returnDate: String? = null,
    val isActive: Boolean = true,
    val fine: Int = 0,
    val daysRemaining: Int = 0,
    val isOverdue: Boolean = false
)

/**
 * Data class representing a Review
 */
data class Review(
    val id: String,
    val bookId: String,
    val userName: String,
    val userInitials: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val likes: Int = 0
)

/**
 * Data class representing a Notification
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val bookTitle: String? = null,
    val actionLabel: String? = null
)

/**
 * Enum for notification types
 */
enum class NotificationType {
    DUE_REMINDER,
    BORROW_SUCCESS,
    RETURN_SUCCESS,
    NEW_BOOK,
    PROMO,
    SYSTEM
}

/**
 * Data class representing a Category
 */
data class Category(
    val id: String,
    val name: String,
    val icon: String // Material icon name
)

/**
 * Data class for Banner/Carousel items
 */
data class Banner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageResId: Int? = null,
    val tag: String,
    val tagColor: String = "primary"
)
