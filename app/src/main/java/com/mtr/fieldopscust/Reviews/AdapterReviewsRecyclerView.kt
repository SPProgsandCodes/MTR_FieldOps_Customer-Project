package com.mtr.fieldopscust.Reviews

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.mtr.fieldopscust.DashboardScreen.Category
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Reviews
import retrofit2.HttpException
import java.io.FileNotFoundException

class AdapterReviewsRecyclerView(private val reviews: List<Reviews>) :
    RecyclerView.Adapter<ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val reviews = reviews[position]
        holder.rName.text = reviews.reviewerName
        holder.rRating.text = reviews.rating.toString()
        holder.rReview.text = reviews.review

        try {
            Glide.with(holder.itemView.context)
                .load(reviews.reviewerPicture)
                .placeholder(R.drawable.placeholder)
                .into(holder.rImage)
        } catch (httpException: HttpException){
            Toast.makeText(holder.itemView.context, "Error Loading Image", Toast.LENGTH_SHORT).show()
            holder.rImage.setImageResource(R.drawable.error)
        } catch (fileNotFoundException: FileNotFoundException){
            Toast.makeText(holder.itemView.context, "Image not found", Toast.LENGTH_SHORT).show()
            holder.rImage.setImageResource(R.drawable.error)
        } catch (glideException: GlideException){
            Toast.makeText(holder.itemView.context, "Glide Exception", Toast.LENGTH_SHORT).show()
            holder.rImage.setImageResource(R.drawable.error)
        }

    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}
