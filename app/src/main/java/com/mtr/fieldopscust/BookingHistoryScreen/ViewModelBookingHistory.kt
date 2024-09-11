package com.mtr.fieldopscust.BookingHistoryScreen

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Job

class ViewModelBookingHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name: TextView = itemView.findViewById(R.id.txt_vendor_work_name_booking_hist)
    var rate: TextView = itemView.findViewById(R.id.txt_price_booking_hist)
    var statusCompleted: AppCompatButton =
        itemView.findViewById(R.id.btn_booking_hist_status_completed)
    var statusPending: AppCompatButton = itemView.findViewById(R.id.btn_booking_hist_status_pending)
    var statusOngoing: AppCompatButton = itemView.findViewById(R.id.btn_booking_hist_status_ongoing)
    var image: ImageView = itemView.findViewById(R.id.imvProfileVendorImgBookingHist)
    var viewMore: TextView = itemView.findViewById(R.id.txt_view_more_booking_hist)


//    fun bind(bookingHistory: Job, listener: AdapterBookingHistory.OnBookingHistoryClickListener){
//        itemView.setOnClickListener {
//            listener.onHistoryClick(bookingHistory)
//        }
//    }
}
