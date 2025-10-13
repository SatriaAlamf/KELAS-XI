package com.jetpackcompose.foodorderingadmin.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun isValidPhone(phone: String): Boolean {
        return phone.isNotBlank() && phone.length >= 10
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }
    
    fun isValidPrice(price: String): Boolean {
        return try {
            price.toDouble() > 0
        } catch (e: Exception) {
            false
        }
    }
    
    fun isValidUrl(url: String): Boolean {
        return url.isNotBlank() && Patterns.WEB_URL.matcher(url).matches()
    }
}

object FormatUtils {
    
    fun formatPrice(price: Double): String {
        return "Rp ${String.format("%,.0f", price)}"
    }
    
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatDateOnly(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun formatOrderNumber(number: Int): String {
        return "ORD${String.format("%06d", number)}"
    }
    
    fun formatPhoneNumber(phone: String): String {
        return when {
            phone.startsWith("0") -> "+62${phone.substring(1)}"
            phone.startsWith("62") -> "+$phone"
            phone.startsWith("+62") -> phone
            else -> phone
        }
    }
    
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} minutes ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            diff < 604800000 -> "${diff / 86400000} days ago"
            else -> formatDateOnly(timestamp)
        }
    }
}
