package com.hari.notiy

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationList: MutableList<NotificationItem>
    private lateinit var textViewStatus: TextView
    private lateinit var buttonNotificationAccess: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        installSplashScreen()

        // Initialize views
        textViewStatus = findViewById(R.id.textViewStatus)
        buttonNotificationAccess = findViewById(R.id.buttonNotificationAccess)
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize notification list and adapter
        notificationList = MyNotificationListener.getNotificationList()
        adapter = NotificationAdapter(groupNotificationsByDate(notificationList))

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set up notification access button
        buttonNotificationAccess.setOnClickListener {
            openNotificationAccessSettings()
        }

        // Update UI based on notification access status
        updateNotificationAccessStatus()
    }

    override fun onResume() {
        super.onResume()
        updateNotificationAccessStatus()
        adapter.updateData(groupNotificationsByDate(notificationList))
    }

    private fun updateNotificationAccessStatus() {
        val isEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
        if (isEnabled) {
            textViewStatus.text = "Notification Access: Enabled"
            textViewStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            buttonNotificationAccess.text = "Settings"
        } else {
            textViewStatus.text = "Notification Access: Disabled"
            textViewStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            buttonNotificationAccess.text = "Enable Access"
        }
    }

    private fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun groupNotificationsByDate(notifications: List<NotificationItem>): List<Any> {
        val groupedList = mutableListOf<Any>()
        val groupedByDate = notifications.groupBy { it.dateString }

        for ((date, notificationsForDate) in groupedByDate) {
            groupedList.add("📅 $date")
            groupedList.addAll(notificationsForDate)
        }

        return groupedList
    }
}
