package com.hari.notiy

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notifications.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NOTIFICATIONS = "notifications"
        private const val COLUMN_ID = "id"
        private const val COLUMN_SENDER = "sender"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NOTIFICATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SENDER TEXT NOT NULL,
                $COLUMN_MESSAGE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        onCreate(db)
    }

    fun insertNotification(sender: String, message: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENDER, sender)
            put(COLUMN_MESSAGE, message)
            put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        }

        val result = db.insert(TABLE_NOTIFICATIONS, null, values)
        db.close()
        return result
    }

    fun getAllNotifications(): MutableList<NotificationItem> {
        val notifications = mutableListOf<NotificationItem>()
        val db = readableDatabase

        // Delete old notifications (older than 10 days)
        deleteOldNotifications()

        val cursor = db.rawQuery(
            "SELECT $COLUMN_SENDER, $COLUMN_MESSAGE, $COLUMN_TIMESTAMP FROM $TABLE_NOTIFICATIONS ORDER BY $COLUMN_TIMESTAMP DESC",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val sender = it.getString(0)
                val message = it.getString(1)
                val timestamp = it.getLong(2)
                notifications.add(NotificationItem(sender, message, timestamp))
            }
        }

        db.close()
        return notifications
    }

    private fun deleteOldNotifications() {
        val db = writableDatabase
        val tenDaysAgo = System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000L)

        db.delete(
            TABLE_NOTIFICATIONS,
            "$COLUMN_TIMESTAMP < ?",
            arrayOf(tenDaysAgo.toString())
        )
        db.close()
    }
}
