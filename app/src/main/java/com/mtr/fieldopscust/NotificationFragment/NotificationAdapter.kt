package com.mtr.fieldopscust.NotificationFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper.humanReadableTimeDifference
import com.mtr.fieldopscust.network.request.Notifications
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.time.Duration

class NotificationAdapter(private val notifications: ArrayList<Notifications>) :
    RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notifications, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notificationItems = notifications[position]
        Glide.with(holder.itemView.context)
            .load(notificationItems.notificationImage)
            .placeholder(R.drawable.placeholder)
            .into(holder.notificationIcon)

        holder.notificationTitle.text = notificationItems.subject
        holder.notificationDescription.text = notificationItems.description
        if (notificationItems.createdAt.isEmpty()){
            holder.notificationTime.text = notificationItems.createdAt
        } else {
            val dateTimeString = notificationItems.createdAt
            holder.notificationTime.text = dateTimeString

            val formatter = DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter()

            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

            holder.notificationTime.text = humanReadableTimeDifference(dateTime)


        }

    }

    private fun getTimeAgo(dateTimeString: String): String {
        // Use ISO_LOCAL_DATE_TIME to parse the provided dateTime string
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val providedDateTime = LocalDateTime.parse(dateTimeString, formatter)

        // Get the current time
        val currentDateTime = LocalDateTime.now()

        // Calculate the duration between the current time and the provided time
        val duration = java.time.Duration.between(providedDateTime, currentDateTime)

        // Convert the duration to seconds, minutes, hours, or days
        val seconds = duration.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24


        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            seconds < 60 -> when {
                seconds < 10 -> "just now"
                else -> "$seconds seconds ago"
            }
            else -> "Just now"
        }
    }

}