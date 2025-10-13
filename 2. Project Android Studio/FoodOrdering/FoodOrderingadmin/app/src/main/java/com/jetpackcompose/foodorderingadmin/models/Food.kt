package com.jetpackcompose.foodorderingadmin.models

data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class Food(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val ingredients: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),
    val preparationTime: Int = 0, // in minutes
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isAvailable: Boolean = true,
    val discount: Int = 0, // percentage
    val tags: List<String> = emptyList(),
    val nutrition: Nutrition? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Nutrition(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0
)

data class Addon(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0
)
