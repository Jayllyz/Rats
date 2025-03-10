package com.rats.ui.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class TrainLinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.trainName)
    val warningIconView: ImageView = itemView.findViewById(R.id.warningIcon)
    val rootView: View = itemView
}
