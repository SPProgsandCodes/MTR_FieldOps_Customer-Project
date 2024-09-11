package com.mtr.fieldopscust.RequestServiceScreen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtr.fieldopscust.R

class AdapterFileNames(private val items: MutableList<Uri>, private val context: Context) :
    RecyclerView.Adapter<AdapterFileNames.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewItemName: TextView = view.findViewById(R.id.textViewItemName)
        val textDeleteItem: TextView = view.findViewById(R.id.textDeleteItem)
        val itemIndex: TextView = view.findViewById(R.id.itemIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_name_rv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = items[position]
        val fileName = getFileName(uri)
        holder.textViewItemName.text = fileName // Display the file name
        holder.itemIndex.text = (position + 1).toString()+"." // Display the item index
        holder.textDeleteItem.setOnClickListener {
            deleteItem(position)
        }
    }

    // Method to delete item
    fun deleteItem(position: Int) {
        items.removeAt(position)  // Remove item from the list
        notifyItemRemoved(position)  // Notify the adapter about the removed item
        notifyItemRangeChanged(position, items.size)  // Optional: notify other items to rebind
    }



    override fun getItemCount(): Int {
        return items.size
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unknown File"
    }
}

