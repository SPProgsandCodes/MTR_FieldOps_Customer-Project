package com.mtr.fieldopscust.DashboardScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.network.request.Job
import retrofit2.HttpException
import java.io.FileNotFoundException

class AdapterCategories(private var categories: List<Category>, private val listener: OnServiceCategoriesClickListener) : RecyclerView.Adapter<CategoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_categories, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    interface OnServiceCategoriesClickListener {
        fun onCategoriesClick(categoriesList: Category)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
//        holder.vendorImage.setImageResource( items[position].getVendorImage())
        val categories = categories[position]
        holder.vendorName.text = categories.name

        holder.bind(categories, listener)

        try {
            Glide.with(holder.itemView.context)
                .load(categories.icon)
                .placeholder(R.drawable.placeholder)
                .into(holder.vendorImage)
        } catch (httpException: HttpException){
            Toast.makeText(holder.itemView.context, "Error Loading Image", Toast.LENGTH_SHORT).show()
            holder.vendorImage.setImageResource(R.drawable.error)
        } catch (fileNotFoundException: FileNotFoundException){
            Toast.makeText(holder.itemView.context, "Image not found", Toast.LENGTH_SHORT).show()
            holder.vendorImage.setImageResource(R.drawable.error)
        } catch (glideException: GlideException){
            Toast.makeText(holder.itemView.context, "Glide Exception", Toast.LENGTH_SHORT).show()
            holder.vendorImage.setImageResource(R.drawable.error)
        }

    }

    fun updateList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }
}