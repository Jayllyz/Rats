package com.rats.ui.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rats.R

class TrainLineReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tv_report_title)
    val type: TextView = itemView.findViewById(R.id.tv_report_type)
    val date: TextView = itemView.findViewById(R.id.tv_report_date)
    val content: TextView = itemView.findViewById(R.id.tv_report_content)
}