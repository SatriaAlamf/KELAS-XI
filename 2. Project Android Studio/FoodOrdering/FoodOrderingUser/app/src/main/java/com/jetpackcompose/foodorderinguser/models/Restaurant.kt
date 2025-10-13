package com.jetpackcompose.foodorderinguser.models

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val foodId: String = "",
    val orderId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val coverImageUrl: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val deliveryTime: String = "",
    val deliveryFee: Double = 0.0,
    val minimumOrder: Double = 0.0,
    val isOpen: Boolean = true,
    val openingHours: Map<String, String> = emptyMap(),
    val cuisineTypes: List<String> = emptyList(),
    val features: List<String> = emptyList(), // Fast delivery, Vegan options, etc.
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)