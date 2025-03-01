package com.rats.ui.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class UserWagonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.tv_user_name)
    val email: TextView = itemView.findViewById(R.id.tv_star)
    val rootView: View = itemView
}
