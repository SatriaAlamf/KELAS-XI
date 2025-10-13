package com.jetpackcompose.foodorderingadmin.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val addresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class AdminUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: AdminRole = AdminRole.STAFF,
    val permissions: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class AdminRole {
    SUPER_ADMIN,
    ADMIN,
    STAFF
}
