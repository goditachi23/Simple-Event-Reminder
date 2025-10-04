package com.example.planpal

data class Event(
    var id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val timestamp: Long
)
