package com.mtr.fieldopscust.BookingHistoryScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtr.fieldopscust.R

import com.mtr.fieldopscust.network.request.Job

class AdapterBookingHistory(private var bookingHistoryList: List<Job>, private val listener: OnBookingHistoryClickListener) :
    RecyclerView.Adapter<ViewModelBookingHistory>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModelBookingHistory {
        return ViewModelBookingHistory(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookings_history_sample, parent, false)
        )
    }

    interface OnBookingHistoryClickListener {
        fun onHistoryClick(job: Job)
    }

    override fun onBindViewHolder(holder: ViewModelBookingHistory, position: Int) {
        val bookingHistory = bookingHistoryList[position]
        Glide.with(holder.itemView.context)
            .load(bookingHistory.documents)
            .placeholder(R.drawable.placeholder)
            .into(holder.image)
        holder.name.text = bookingHistory.name
        val price: Float = bookingHistory.price.toFloat()
        val formattedPrice = String.format("%.2f", price)

        holder.rate.text = "$$formattedPrice"
        val status = bookingHistory.status

        holder.viewMore.setOnClickListener{
            listener.onHistoryClick(bookingHistory)
        }



        if (status == "Requested") {
            holder.statusCompleted.text = status
            holder.statusCompleted.visibility = ViewGroup.VISIBLE
            holder.statusPending.visibility = ViewGroup.GONE
            holder.statusOngoing.visibility = ViewGroup.GONE
        }else if(status == "Completed"){
            holder.statusCompleted.text = status
            holder.statusCompleted.visibility = ViewGroup.VISIBLE
            holder.statusPending.visibility = ViewGroup.GONE
            holder.statusOngoing.visibility = ViewGroup.GONE
        } else if (status == "Pending") {
            holder.statusCompleted.visibility = ViewGroup.GONE
            holder.statusPending.visibility = ViewGroup.VISIBLE
            holder.statusOngoing.visibility = ViewGroup.GONE
        } else if (status == "Ongoing") {
            holder.statusCompleted.visibility = ViewGroup.GONE
            holder.statusPending.visibility = ViewGroup.GONE
            holder.statusOngoing.visibility = ViewGroup.VISIBLE
        } else if (status == "Inprogress"){
            holder.statusCompleted.text = "Ongoing"
            holder.statusCompleted.visibility = ViewGroup.GONE
            holder.statusPending.visibility = ViewGroup.GONE
            holder.statusOngoing.visibility = ViewGroup.VISIBLE
        }
    }

    fun updateList(newList: List<Job>) {
        bookingHistoryList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bookingHistoryList.size
    }

    // Method to update the data in the adapter
    fun updateData(newBookingHistory: List<Job>) {
        bookingHistoryList = newBookingHistory
        notifyDataSetChanged() // Notify the adapter that data has changed
    }

}
