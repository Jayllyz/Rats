package com.rats.ui.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class PassengerCommentsViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.tv_user_name)
    val stars = itemView.findViewById<TextView>(R.id.tv_stars)
    val comment = itemView.findViewById<TextView>(R.id.tv_comment)
    val date = itemView.findViewById<TextView>(R.id.tv_date)
}
