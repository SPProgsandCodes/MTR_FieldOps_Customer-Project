package com.mtr.fieldopscust.ChatScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.MessageItem
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

class MessageAdapter(private val messages: List<MessageItem>
, private val currentUserId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define constants for view types
    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.sendBy == currentUserId) {
            VIEW_TYPE_SENT // Sent message
        } else {
            VIEW_TYPE_RECEIVED // Received message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            // Inflate the layout for sent messages
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            // Inflate the layout for received messages
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // ViewHolder for sent messages
    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: MessageItem) {
            itemView.findViewById<TextView>(R.id.sentMessageText).text = message.message

            val dateTimeString = message.sendTime

            val formatter = DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter()

            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

            itemView.findViewById<TextView>(R.id.sentTimeText).text = humanReadableTimeDifference(dateTime)
        }

        fun humanReadableTimeDifference(dateTime: LocalDateTime): String {
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val durationSeconds = ChronoUnit.SECONDS.between(dateTime, now)
            val durationMinutes = ChronoUnit.MINUTES.between(dateTime, now)

            return when {
                durationSeconds < 60 -> when {
                    durationSeconds < 10 -> "just now"
                    else -> "$durationSeconds seconds ago"
                }
                durationMinutes < 60 -> "$durationMinutes minutes ago"
                durationMinutes < 1440 -> "${durationMinutes / 60} hours ago"
                durationMinutes < 10080 -> "${durationMinutes / 1440} days ago"
                else -> dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            }
        }
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: MessageItem) {
            itemView.findViewById<TextView>(R.id.receivedMessageText).text = message.message

            itemView.findViewById<TextView>(R.id.senderNameText).text = message.senderName

            val dateTimeString = message.sendTime

            val formatter = DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .toFormatter()

            val dateTime = LocalDateTime.parse(dateTimeString, formatter)

            itemView.findViewById<TextView>(R.id.receivedTimeText).text = humanReadableTimeDifference(dateTime)
        }

        fun humanReadableTimeDifference(dateTime: LocalDateTime): String {
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val durationSeconds = ChronoUnit.SECONDS.between(dateTime, now)
            val durationMinutes = ChronoUnit.MINUTES.between(dateTime, now)

            return when {
                durationSeconds < 60 -> when {
                    durationSeconds < 10 -> "just now"
                    else -> "$durationSeconds seconds ago"
                }
                durationMinutes < 60 -> "$durationMinutes minutes ago"
                durationMinutes < 1440 -> "${durationMinutes / 60} hours ago"
                durationMinutes < 10080 -> "${durationMinutes / 1440} days ago"
                else -> dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            }
        }
    }


}
