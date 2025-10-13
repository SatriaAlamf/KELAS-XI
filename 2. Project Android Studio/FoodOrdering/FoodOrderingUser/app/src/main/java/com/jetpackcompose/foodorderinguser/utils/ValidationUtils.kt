package com.jetpackcompose.foodorderinguser.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun isValidPhone(phone: String): Boolean {
        return phone.isNotEmpty() && phone.length >= 10 && phone.all { it.isDigit() }
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.length >= 2 && name.all { it.isLetter() || it.isWhitespace() }
    }
    
    fun isValidZipCode(zipCode: String): Boolean {
        return zipCode.isNotEmpty() && zipCode.length in 5..6 && zipCode.all { it.isDigit() }
    }
    
    fun isValidAddress(address: String): Boolean {
        return address.isNotEmpty() && address.length >= 10
    }
}

object FormatUtils {
    
    fun formatPrice(price: Double): String {
        return String.format("$%.2f", price)
    }
    
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }
    
    fun formatDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        return format.format(date)
    }
    
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(date)
    }
    
    fun formatOrderNumber(orderNumber: String): String {
        return "#$orderNumber"
    }
    
    fun formatPhone(phone: String): String {
        return if (phone.length == 10) {
            "${phone.substring(0, 3)}-${phone.substring(3, 6)}-${phone.substring(6)}"
        } else phone
    }
    
    fun formatRating(rating: Double): String {
        return String.format("%.1f", rating)
    }
    
    fun formatPreparationTime(minutes: Int): String {
        return if (minutes < 60) {
            "$minutes min"
        } else {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes == 0) {
                "$hours hr"
            } else {
                "$hours hr $remainingMinutes min"
            }
        }
    }
    
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days < 7 -> "$days day${if (days == 1L) "" else "s"} ago"
            else -> formatDate(timestamp)
        }
    }
}