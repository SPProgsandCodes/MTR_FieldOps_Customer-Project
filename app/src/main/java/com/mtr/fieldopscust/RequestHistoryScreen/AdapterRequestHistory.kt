package com.mtr.fieldopscust.RequestHistoryScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mtr.fieldopscust.Utils.ApplicationHelper.convertDate
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Job

class AdapterRequestHistory(private val requestHistory: List<Job>, private val listener: FragmentRequestHistory) :
    RecyclerView.Adapter<RequestHistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHistoryViewHolder {
        return RequestHistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_request_history, parent, false)
        )
    }

//    interface OnRequestHistoryClickListener {
//        fun onRequestHistClick(job: Job)
//    }

    interface onViewStatusClickListener {
        fun onViewStatusClick(job: Job)
    }

    override fun onBindViewHolder(holder: RequestHistoryViewHolder, position: Int) {
        val requestHistory = requestHistory[position]
        val price: Float = requestHistory.price.toFloat()
        val formattedPrice = String.format("%.2f", price)
        holder.vName.text = requestHistory.name
        holder.vDate.text = convertDate(requestHistory.updatedAt)
        holder.vPrice.text = "$${formattedPrice}"
        Glide.with(holder.itemView.context)
            .load(requestHistory.documents)
            .placeholder(R.drawable.placeholder)
            .into(holder.vImage)
//        holder.vImage.setImageResource(R.drawable.electric_wires)
        holder.vStatus.text = requestHistory.status
        holder.btnViewStatus.setOnClickListener{
            listener.onViewStatusClick(requestHistory)

//            holder.bind(requestHistory, listener)
        }

    }

    override fun getItemCount(): Int {
        return requestHistory.size
    }
}
