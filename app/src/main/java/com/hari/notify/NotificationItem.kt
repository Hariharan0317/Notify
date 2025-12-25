package com.hari.notiy

import java.text.SimpleDateFormat
import java.util.*

data class NotificationItem(
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val dateString: String
        get() {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }

    val timeString: String
        get() {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return timeFormat.format(Date(timestamp))
        }
}
