package com.example.eventreminder

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.eventreminder.databinding.DialogAddEventBinding
import java.text.SimpleDateFormat
import java.util.*

class AddEventDialog(
    context: Context,
    private val onEventAdded: (String, String, String) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogAddEventBinding
    private var selectedDate = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDialog()
    }

    private fun setupDialog() {
        setTitle("Add New Event")

        binding.buttonSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonSelectTime.setOnClickListener {
            showTimePicker()
        }

        binding.buttonSave.setOnClickListener {
            saveEvent()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
                binding.buttonSelectDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Don't allow past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minute)

                selectedTime = timeFormat.format(selectedCalendar.time)
                binding.buttonSelectTime.text = selectedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun saveEvent() {
        val title = binding.editTextTitle.text.toString().trim()

        when {
            title.isEmpty() -> {
                Toast.makeText(context, "Please enter event title", Toast.LENGTH_SHORT).show()
            }
            selectedDate.isEmpty() -> {
                Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show()
            }
            selectedTime.isEmpty() -> {
                Toast.makeText(context, "Please select time", Toast.LENGTH_SHORT).show()
            }
            else -> {
                onEventAdded(title, selectedDate, selectedTime)
                dismiss()
            }
        }
    }
}
