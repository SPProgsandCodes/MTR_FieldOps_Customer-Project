package com.mtr.fieldopscust.RequestHistoryScreen

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.BookingHistoryScreen.AdapterBookingHistory
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Job

class RequestHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var vName: TextView = itemView.findViewById(R.id.txt_vendor_work_name)
    var vPrice: TextView = itemView.findViewById(R.id.txt_vendor_price)
    var vDate: TextView = itemView.findViewById(R.id.txt_vendor_date)
    var vImage: ImageView = itemView.findViewById(R.id.imvProfileVendorImg)
    var vStatus: TextView = itemView.findViewById(R.id.btn_request_history)
    var btnViewStatus : AppCompatButton = itemView.findViewById(R.id.btn_view_status)

//    fun bind(requestHistory: Job, listener: AdapterRequestHistory.OnRequestHistoryClickListener){
//        itemView.setOnClickListener {
//            listener.onRequestHistClick(requestHistory)
//        }
//    }
}