package com.example.eventreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.planpal.Event
import com.example.planpal.NotificationReceiver
import java.text.SimpleDateFormat
import java.util.*

object NotificationScheduler {

    fun scheduleNotification(context: Context, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("event_title", event.title)
            putExtra("event_id", event.id.toInt())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Parse the date and time
        val dateTimeString = "${event.date} ${event.time}"
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

        try {
            val eventDateTime = dateFormat.parse(dateTimeString)
            eventDateTime?.let {
                // For testing: Schedule notification 1 minute from now instead of 15 minutes before
                val notificationTime = System.currentTimeMillis() + (1 * 60 * 1000) // 1 minute for testing

                Log.d("EventReminder", "Scheduling notification for: ${Date(notificationTime)}")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("EventReminder", "Error scheduling notification", e)
        }
    }


    fun cancelNotification(context: Context, eventId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
