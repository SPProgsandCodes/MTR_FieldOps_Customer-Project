package com.mtr.fieldopscust.NotificationFragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R
import org.w3c.dom.Text

class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var notificationTitle: TextView = itemView.findViewById(R.id.notification_title)
    var notificationDescription: TextView = itemView.findViewById(R.id.notification_description)
    var notificationTime: TextView = itemView.findViewById(R.id.notification_time)
    var notificationIcon: ImageView = itemView.findViewById(R.id.img_profile_pic_notifications)
}