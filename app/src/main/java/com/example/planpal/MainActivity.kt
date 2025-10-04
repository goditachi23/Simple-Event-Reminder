// D:/Simple Event Reminder/app/src/main/java/com/example/planpal/MainActivity.kt

package com.example.planpal

import android.Manifest
import android.app.AlarmManager // You will also need this for requestExactAlarmPermission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent // And this one
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings // And this one
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat // <-- ADD THIS LINE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventreminder.AddEventDialog
import com.example.eventreminder.EventAdapter
import com.example.eventreminder.NotificationScheduler
import com.example.eventreminder.R
import com.example.eventreminder.databinding.ActivityMainBinding
import com.example.planpal.DatabaseHelper
import com.example.planpal.Event

// ... rest of your class


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventList: MutableList<Event>
    private lateinit var dbHelper: DatabaseHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            createNotificationChannel()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database
        dbHelper = DatabaseHelper(this)

        requestExactAlarmPermission()
        dbHelper = DatabaseHelper(this)
        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Setup RecyclerView
        setupRecyclerView()

        binding.fabAddEvent.setOnLongClickListener {
            testNotification()
            true
        }

        // Load events from database
        loadEvents()

        // Setup add event button
        binding.fabAddEvent.setOnClickListener {
            showAddEventDialog()
        }
    }

    private fun testNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, "EVENT_REMINDER")
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(999, notification)
    }
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    createNotificationChannel()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            createNotificationChannel()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Reminders"
            val descriptionText = "Channel for event reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("EVENT_REMINDER", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupRecyclerView() {
        eventList = mutableListOf()
        eventAdapter = EventAdapter(eventList) { event ->
            // Delete event
            dbHelper.deleteEvent(event.id)
            loadEvents()
            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewEvents.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = eventAdapter
        }
    }

    private fun loadEvents() {
        eventList.clear()
        eventList.addAll(dbHelper.getAllEvents())
        eventAdapter.notifyDataSetChanged()

        binding.textEmptyState.visibility = if (eventList.isEmpty())
            android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun showAddEventDialog() {
        val dialog = AddEventDialog(this) { title, date, time ->
            // Create new event
            val event = Event(0, title, date, time, System.currentTimeMillis())

            // Save to database
            val id = dbHelper.addEvent(event)
            event.id = id

            // Schedule notification
            NotificationScheduler.scheduleNotification(this, event)

            // Refresh list
            loadEvents()

            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }
}
