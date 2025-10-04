package com.example.eventreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventreminder.databinding.ItemEventBinding
import com.example.planpal.Event

class EventAdapter(
    private val events: List<Event>,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, onDeleteClick: (Event) -> Unit) {
            binding.textTitle.text = event.title
            binding.textDate.text = event.date
            binding.textTime.text = event.time

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], onDeleteClick)
    }

    override fun getItemCount(): Int = events.size
}
