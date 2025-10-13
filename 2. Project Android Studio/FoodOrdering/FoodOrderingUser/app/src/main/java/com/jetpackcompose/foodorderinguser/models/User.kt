package com.jetpackcompose.foodorderinguser.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Address(
    val id: String = "",
    val title: String = "", // Home, Office, etc.
    val fullAddress: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isDefault: Boolean = false
)