package com.mtr.fieldopscust.Reviews

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var rName: TextView = itemView.findViewById(R.id.profile_name)
//    var rDate: TextView = itemView.findViewById(R.id.review_date)
    var rReview: TextView = itemView.findViewById(R.id.text_reviews)
    var rRating: TextView = itemView.findViewById(R.id.rating_text)
    var rImage: ImageView = itemView.findViewById(R.id.imvProfile)
}
