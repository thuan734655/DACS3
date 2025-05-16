package com.example.dacs3.utils

import java.util.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * Utility class for date and time formatting
 */
object DateTimeUtils {
    
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fullFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    
    /**
     * Format a date to a readable string
     */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    /**
     * Format a time to a readable string
     */
    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }
    
    /**
     * Format a date and time to a readable string
     */
    fun formatDateTime(date: Date): String {
        return fullFormat.format(date)
    }
    
    /**
     * Format a date relative to current time (e.g. "2 hours ago", "Yesterday", etc.)
     */
    fun formatRelativeTime(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        
        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            hours < 24 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days days ago"
            else -> formatDate(date)
        }
    }
}
