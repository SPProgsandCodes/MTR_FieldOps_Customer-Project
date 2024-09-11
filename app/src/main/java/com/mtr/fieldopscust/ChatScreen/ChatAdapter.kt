package com.mtr.fieldopscust.ChatScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.ResultMessageSended
import com.mtr.fieldopscust.network.request.SendMessageResponse
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ChatAdapter(
    private var messageModels: MutableList<ResultMessageSended>,
    private var currentUserId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SENDER_VIEW_TYPE) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.sample_chat_sender, parent, false)
            return SenderViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.sample_chat_receiver, parent, false)
            return ReceiverViewHolder(view)
        }
    }
    fun addMessage(message: ResultMessageSended) {
        messageModels.add(message)
        notifyItemInserted(messageModels.size - 1)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = messageModels[position]

        if (holder is SenderViewHolder) {
            holder.senderMsg.text = messages.message
            val timeString = messages.sendTime
            // Define the output pattern (e.g., "hh:mm a" for 12-hour format with AM/PM)
            val outputPattern = "hh:mm a"  // Output format for 12-hour format with AM/PM
            holder.senderDate.text = formatTime(timeString, outputPattern)
        } else if (holder is ReceiverViewHolder){
            val timeString = messages.sendTime
            // Define the output pattern (e.g., "hh:mm a" for 12-hour format with AM/PM)
            val outputPattern = "hh:mm a"  // Output format for 12-hour format with AM/PM
            holder.receiverMsg.text = messages.message
            holder.receiverDate.text = formatTime(timeString, outputPattern)
        }
    }



    override fun getItemViewType(position: Int): Int {
        val message = messageModels[position]
        val viewType: Int = if (message.sendBy == currentUserId) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }

        return viewType
    }

    override fun getItemCount(): Int {
        return messageModels.size
    }


    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderMsg: TextView = itemView.findViewById(R.id.sender_msg)
        var senderDate: TextView = itemView.findViewById(R.id.sender_msg_date)
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var receiverMsg: TextView = itemView.findViewById(R.id.receiver_msg)
        var receiverDate: TextView = itemView.findViewById(R.id.receiver_msg_date)
    }

    companion object {
        const val SENDER_VIEW_TYPE: Int = 1
        const val RECEIVER_VIEW_TYPE: Int = 2
    }

    private fun formatTime(dateTimeString: String, outputPattern: String): String {
        // Define the input formatter to parse the given date-time string
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        // Parse the date-time string to an OffsetDateTime object
        val dateTime = OffsetDateTime.parse(dateTimeString, inputFormatter)

        // Extract the LocalTime from the OffsetDateTime
        val time = dateTime.toLocalTime()

        // Define the output formatter with the desired pattern
        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern)

        // Format the LocalTime object to the desired format
        return time.format(outputFormatter)
    }
}
