package com.example.dacs3.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Format a Date object to a readable string
 */
fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

/**
 * Convert a string to Date object
 */
fun String.toDate(pattern: String = "dd MMM yyyy"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}