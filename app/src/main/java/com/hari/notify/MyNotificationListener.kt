package com.hari.notiy

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class MyNotificationListener : NotificationListenerService() {

    companion object {
        private lateinit var databaseHelper: DatabaseHelper

        fun getNotificationList(): MutableList<NotificationItem> {
            return if (::databaseHelper.isInitialized) {
                databaseHelper.getAllNotifications()
            } else {
                mutableListOf()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        databaseHelper = DatabaseHelper(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName

        // Filter for WhatsApp notifications
        if (packageName == "com.whatsapp") {
            val notification = sbn.notification
            val extras = notification.extras

            val sender = extras.getString("android.title") ?: "Unknown"
            val message = extras.getString("android.text") ?: "No message"

            // Save to database
            databaseHelper.insertNotification(sender, message)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }
}
