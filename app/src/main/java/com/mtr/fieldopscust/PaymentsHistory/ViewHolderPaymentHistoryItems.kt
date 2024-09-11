package com.mtr.fieldopscust.PaymentsHistory

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R

class ViewHolderPaymentHistoryItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imvPaymentSender: ImageView = itemView.findViewById(R.id.imv_Payment_Sender_Image)
    var sender_name: TextView = itemView.findViewById(R.id.txt_Payment_Sender_Name)
    var sender_amount: TextView = itemView.findViewById(R.id.txt_payment_rate)
    var sender_date: TextView = itemView.findViewById(R.id.txt_payment_date)
    var payment_status: TextView = itemView.findViewById(R.id.txt_payment_status)
    var service_provider: TextView = itemView.findViewById(R.id.txt_service_provider_name)
}
