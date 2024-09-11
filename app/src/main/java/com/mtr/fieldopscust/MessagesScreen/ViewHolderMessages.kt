package com.mtr.fieldopscust.MessagesScreen

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.BookingHistoryScreen.AdapterBookingHistory
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.ChatUser
import com.mtr.fieldopscust.network.request.Job

class ViewHolderMessages(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imvProfile: ImageView = itemView.findViewById(R.id.img_profile_pic_messages)
    var name: TextView = itemView.findViewById(R.id.profile_msg_sender_name)
    var message: TextView = itemView.findViewById(R.id.profile_msg)
    var date: TextView = itemView.findViewById(R.id.profile_msg_date)


    fun bind(messages: ChatUser, listener: AdapterUserMessages.OnMessageClickListener){
        itemView.setOnClickListener {
            listener.onMessageClick(messages)
        }
    }


}
