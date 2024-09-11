package com.mtr.fieldopscust.DashboardScreen

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.BookingHistoryScreen.AdapterBookingHistory
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Job

class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var vendorName: TextView = itemView.findViewById(R.id.electrician)
    var vendorImage: ImageView = itemView.findViewById(R.id.img_electrician_symbol)

    fun bind(categoriesList: Category, listener: AdapterCategories.OnServiceCategoriesClickListener){
        itemView.setOnClickListener {
            listener.onCategoriesClick(categoriesList)
        }
    }
}