package com.rats.ui.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class UserWagonViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.tv_user_name)
    val email = itemView.findViewById<TextView>(R.id.tv_user_email)
}
