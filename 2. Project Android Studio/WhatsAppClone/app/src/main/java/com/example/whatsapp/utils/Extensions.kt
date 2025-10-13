package com.example.whatsapp.utils

import android.content.Context
import android.text.format.DateUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import java.text.SimpleDateFormat
import java.util.*

object Extensions {
    
    fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(this)
    }
    
    fun ImageView.loadCircularImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .transform(CircleCrop())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(this)
    }
    
    fun Date.toTimeString(): String {
        val now = Date()
        val diff = now.time - this.time
        
        return when {
            diff < DateUtils.MINUTE_IN_MILLIS -> "now"
            diff < DateUtils.HOUR_IN_MILLIS -> "${diff / DateUtils.MINUTE_IN_MILLIS}m"
            diff < DateUtils.DAY_IN_MILLIS -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
            diff < DateUtils.WEEK_IN_MILLIS -> SimpleDateFormat("EEE", Locale.getDefault()).format(this)
            else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(this)
        }
    }
    
    fun Date.toDetailedTimeString(): String {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = this
        
        val nowCalendar = Calendar.getInstance()
        nowCalendar.time = now
        
        return when {
            isSameDay(calendar, nowCalendar) -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
            isYesterday(calendar, nowCalendar) -> "Yesterday ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)}"
            else -> SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(this)
        }
    }
    
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun isYesterday(messageDate: Calendar, now: Calendar): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.time = now.time
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        
        return isSameDay(messageDate, yesterday)
    }
    
    fun String.isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    
    fun String.isValidPhoneNumber(): Boolean {
        return android.util.Patterns.PHONE.matcher(this).matches() && this.length >= 10
    }
    
    fun Long.formatFileSize(): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = this.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return String.format("%.1f %s", size, units[unitIndex])
    }
    
    fun Long.formatDuration(): String {
        val seconds = this / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60)
            else -> String.format("%d:%02d", minutes, seconds % 60)
        }
    }
}