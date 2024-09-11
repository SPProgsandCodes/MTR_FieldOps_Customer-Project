package com.mtr.fieldopscust.DialogRequestStatus

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R

class ViewHolderRequestStatusDialog(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var date: TextView = itemView.findViewById(R.id.txt_date)
    var time: TextView = itemView.findViewById(R.id.txt_time)
    var statusWhiteText: TextView = itemView.findViewById(R.id.txt_status_white)
    var statusGreyText: TextView = itemView.findViewById(R.id.txt_status_grey)
    var message1: TextView = itemView.findViewById(R.id.txt_request_has_been_viewed_static)
    var message2: TextView = itemView.findViewById(R.id.txt_request_viewed_by)
    var imgGreenCircleDrawable: ImageView = itemView.findViewById(R.id.green_dot)
    var imgGreyCircleDrawable: ImageView = itemView.findViewById(R.id.grey_dot)
    var imgViewEyeorTick: ImageView = itemView.findViewById(R.id.drawable_status_white)
    var imgViewEyeorTickGrey: ImageView = itemView.findViewById(R.id.drawable_status_grey)
    var btnChat: LinearLayout = itemView.findViewById(R.id.btn_chat_with_support)
    var viewLineGreen : View = itemView.findViewById(R.id.viewLineGreen)
    var viewLineGrey : View = itemView.findViewById(R.id.viewLineGrey)
    var layoutStatus : RelativeLayout = itemView.findViewById(R.id.layout_status)
}
