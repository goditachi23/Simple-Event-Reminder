package com.example.eventreminder

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventreminder.databinding.ActivityMainBinding
import com.example.planpal.DatabaseHelper
import com.example.planpal.Event
import java.text.SimpleDateFormat
import java.util.*

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

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Setup RecyclerView
        setupRecyclerView()

        // Load events from database
        loadEvents()

        // Setup add event button
        binding.fabAddEvent.setOnClickListener {
            showAddEventDialog()
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
