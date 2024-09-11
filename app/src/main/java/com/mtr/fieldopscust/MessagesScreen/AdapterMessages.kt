//package com.mtr.fieldopscust.MessagesScreen
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.mtr.fieldopscust.Utils.ApplicationHelper.formatDateString
//import com.mtr.fieldopscust.R
//import com.mtr.fieldopscust.network.request.ChatUser
//import com.mtr.fieldopscust.network.request.Message
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class AdapterMessages(
//    private val messages: ArrayList<ChatUser>,
//    private val currentUserId: Int,
//    private val listener: OnMessageClickListener,
//    private val noMessagesTextView: TextView
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val VIEW_TYPE_SENDER = 1
//    private val VIEW_TYPE_RECEIVER = 2
//    private val filteredMessages: List<ChatUser> = messages
//
//    interface OnMessageClickListener {
//        fun onMessageClick(message: ChatUser)
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val message = filteredMessages[position]
//        return if (message.sendBy == currentUserId) {
//            VIEW_TYPE_SENDER
//        } else {
//            VIEW_TYPE_RECEIVER
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//
//        return if (viewType == VIEW_TYPE_SENDER) {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_messages_sample, parent, false)
//            SenderViewHolder(view)
//        } else {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_messages_sample, parent, false)
//            ReceiverViewHolder(view)
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = filteredMessages[position]
//
//        if (holder is SenderViewHolder) {
//            holder.bind(item, listener)
//        } else if (holder is ReceiverViewHolder) {
//            holder.bind(item, listener)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        val itemCount = filteredMessages.size
//        if (itemCount == 0) {
//            // Show the "No new messages" TextView
//            noMessagesTextView.text = "No new messages"
//            noMessagesTextView.visibility = View.VISIBLE
//        } else {
//            noMessagesTextView.text = "No new messages"
//            noMessagesTextView.visibility = View.GONE
//        }
//        return filteredMessages.size
//    }
//
//    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val imgProfilePicture: ImageView = itemView.findViewById(R.id.img_profile_pic_messages)
//        private val tvSenderName: TextView = itemView.findViewById(R.id.profile_msg_sender_name)
//        private val tvSenderMessage: TextView = itemView.findViewById(R.id.profile_msg)
//        private val tvTimeandDate: TextView = itemView.findViewById(R.id.profile_msg_date)
//
//        fun bind(message: Message, listener: OnMessageClickListener) {
//            imgProfilePicture.setImageResource(R.drawable.img_guy_hawkins)
//            tvSenderName.text = message.userDetailsSendBy.firstName
//            tvTimeandDate.text = formatDateString(message.sendTime)
//            tvSenderMessage.text = message.message
//
//            itemView.setOnClickListener {
//                listener.onMessageClick(message)
//            }
//        }
//
//        private fun formatDateString(dateString: String): String {
//            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//            val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
//
//            val date: Date? = inputFormat.parse(dateString)
//
//            return date?.let { outputFormat.format(it) } ?: dateString
//        }
//    }
//
//    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val imgProfilePicture: ImageView = itemView.findViewById(R.id.img_profile_pic_messages)
//        private val tvSenderName: TextView = itemView.findViewById(R.id.profile_msg_sender_name)
//        private val tvSenderMessage: TextView = itemView.findViewById(R.id.profile_msg)
//        private val tvTimeandDate: TextView = itemView.findViewById(R.id.profile_msg_date)
//
//        fun bind(message: Message, listener: OnMessageClickListener) {
//            imgProfilePicture.setImageResource(R.drawable.img_guy_hawkins)
//            tvSenderName.text = message.userDetailsSendBy.firstName
//            tvTimeandDate.text = formatDateString(message.sendTime)
//            tvSenderMessage.text = message.message
//
//            itemView.setOnClickListener{
//                listener.onMessageClick(message)
//            }
//        }
//
//
//    }
//
//    private fun filterMessages(messages: List<Message>): List<Message> {
//        val latestMessages = mutableMapOf<Int, Message>()
//        for (message in messages) {
//            if (message.sendBy == currentUserId){
//                val userId = message.sendTo
//                val existingMessage = latestMessages[userId]
//
//                // If there is no existing message or if the current message is newer, update the map
//                if (existingMessage == null || isNewer(message.sendTime, existingMessage.sendTime)) {
//                    latestMessages[userId] = message
//                }
//            }
//
//        }
//        return latestMessages.values.sortedByDescending { it.sendTime }
//    }
//
//    private fun isNewer(dateString1: String, dateString2: String): Boolean {
//        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//        val date1: Date? = format.parse(dateString1)
//        val date2: Date? = format.parse(dateString2)
//        return date1?.after(date2) == true
//    }
//}
