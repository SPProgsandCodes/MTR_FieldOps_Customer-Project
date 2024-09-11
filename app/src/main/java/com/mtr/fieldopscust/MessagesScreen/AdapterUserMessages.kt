package com.mtr.fieldopscust.MessagesScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper.humanReadableTimeDifference
import com.mtr.fieldopscust.network.request.ChatUser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class AdapterUserMessages(
    private val messages: ArrayList<ChatUser>,
    private val currentUserId: Int,
    private val listener: OnMessageClickListener,
    private val noMessagesTextView: TextView
) : RecyclerView.Adapter<ViewHolderMessages>() {

    interface OnMessageClickListener {
        fun onMessageClick(message: ChatUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMessages {
        return ViewHolderMessages(
            LayoutInflater.from(parent.context).inflate(R.layout.item_messages_sample, parent, false)
        )
    }

    override fun getItemCount(): Int {
        val itemCount = messages.size
        if (itemCount == 0) {
            // Show the "No new messages" TextView
            noMessagesTextView.text = "No new messages"
            noMessagesTextView.visibility = View.VISIBLE
        } else {
            noMessagesTextView.text = "No new messages"
            noMessagesTextView.visibility = View.GONE
        }
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolderMessages, position: Int) {
        val messagesList = messages[position]
        holder.bind(messagesList, listener)

        Glide.with(holder.itemView.context)
            .load(messagesList.profilePicture)
            .placeholder(R.drawable.placeholder)
            .into(holder.imvProfile)

        holder.name.text = messagesList.userName
        holder.message.text = messagesList.lastMessage.message
        val lastMessageTime = messagesList.lastMessage.sendTime
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter()

        val dateTime = LocalDateTime.parse(lastMessageTime, formatter)
        holder.date.text = humanReadableTimeDifference(dateTime)







    }
}