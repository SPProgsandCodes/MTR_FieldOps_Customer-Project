package com.mtr.fieldopscust.PaymentsHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.Utils.ApplicationHelper.formatDateString
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.History


class AdapterPaymentHistoryItems(private val transactionHistory: List<History>) :
    RecyclerView.Adapter<ViewHolderPaymentHistoryItems>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderPaymentHistoryItems {
        return ViewHolderPaymentHistoryItems(
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolderPaymentHistoryItems, position: Int) {
        var transactionHistoryItems = transactionHistory[position]
        val price: Float = transactionHistoryItems.amount.toFloat()
        val formattedPrice = String.format("%.2f", price)
        holder.imvPaymentSender.setImageResource(R.drawable.menface)
        holder.sender_name.text = transactionHistoryItems.serviceType
        holder.sender_date.text = formatDateString(transactionHistoryItems.createdAt)
        holder.sender_amount.text = "$$formattedPrice"
        holder.payment_status.text = transactionHistoryItems.status
        holder.service_provider.text = transactionHistoryItems.serviceProvider
    }

    override fun getItemCount(): Int {
        return transactionHistory.size
    }
}
