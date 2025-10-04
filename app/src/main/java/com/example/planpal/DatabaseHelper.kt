package com.example.planpal // Corrected to match the directory structure

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EventReminder.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_EVENTS = "events"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DATE = "date"
        private const val KEY_TIME = "time"
        private const val KEY_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_EVENTS (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TITLE TEXT NOT NULL,
                $KEY_DATE TEXT NOT NULL,
                $KEY_TIME TEXT NOT NULL,
                $KEY_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    fun addEvent(event: Event): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, event.title)
            put(KEY_DATE, event.date)
            put(KEY_TIME, event.time)
            put(KEY_TIMESTAMP, event.timestamp)
        }

        val id = db.insert(TABLE_EVENTS, null, values)
        db.close()
        return id
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EVENTS ORDER BY $KEY_TIMESTAMP DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                val event = Event(
                    id = it.getLong(it.getColumnIndexOrThrow(KEY_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(KEY_TITLE)),
                    date = it.getString(it.getColumnIndexOrThrow(KEY_DATE)),
                    time = it.getString(it.getColumnIndexOrThrow(KEY_TIME)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(KEY_TIMESTAMP))
                )
                events.add(event)
            }
        }

        db.close()
        return events
    }

    fun deleteEvent(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_EVENTS, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}