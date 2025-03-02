package com.rats.ui.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val userName: TextView = itemView.findViewById(R.id.user_name)
    val messageContent: TextView = itemView.findViewById(R.id.message_content)
    val messageDate: TextView = itemView.findViewById(R.id.message_date)
}
