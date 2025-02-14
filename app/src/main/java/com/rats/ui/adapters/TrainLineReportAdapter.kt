package com.rats.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.Report
import com.rats.ui.viewHolders.TrainLineReportViewHolder

class TrainLineReportAdapter(
    private val reports: List<Report>,
) : RecyclerView.Adapter<TrainLineReportViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrainLineReportViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_line_report_view_holder, parent, false)
        return TrainLineReportViewHolder(view)
    }

    override fun getItemCount(): Int = reports.size

    override fun onBindViewHolder(
        holder: TrainLineReportViewHolder,
        position: Int,
    ) {
        val report = reports[position]

        val month = report.createdAt.split("T")[0].split("-")[1]
        val day = report.createdAt.split("T")[0].split("-")[2]
        val hour = report.createdAt.split("T")[1].split(":")[0]
        val minute = report.createdAt.split("T")[1].split(":")[1]
        val date =
            buildString {
                append(day)
                append("/")
                append(month)
                append(" à ")
                append(hour)
                append(":")
                append(minute)
            }

        holder.title.text = report.title
        holder.type.text = report.reportType
        holder.date.text = date
        holder.content.text = report.description
    }
}
