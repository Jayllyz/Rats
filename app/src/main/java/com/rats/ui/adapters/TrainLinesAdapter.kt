package com.rats.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.TrainLines
import com.rats.ui.activities.TrainLineDetailActivity
import com.rats.ui.viewHolders.TrainLinesViewHolder

class TrainLinesAdapter(
    private val trainLineStates: List<TrainLines>,
    private val context: Context,
) : RecyclerView.Adapter<TrainLinesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrainLinesViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transport_line_view_holder, parent, false)
        return TrainLinesViewHolder(view)
    }

    override fun getItemCount(): Int = trainLineStates.size

    override fun onBindViewHolder(
        holder: TrainLinesViewHolder,
        position: Int,
    ) {
        val trainLineState = trainLineStates[position]

        holder.name.text = trainLineState.name
        holder.warningIconView.setImageResource(
            when (trainLineState.status) {
                "safe" -> R.drawable.safe_notification
                "danger" -> R.drawable.warning_icon
                else -> R.drawable.warning_notification
            },
        )

        holder.warningIconView.contentDescription =
            "{trainLineState.status}"

        holder.rootView.setOnClickListener {
            val intent =
                Intent(context, TrainLineDetailActivity::class.java).apply {
                    putExtra("id", trainLineState.id)
                }
            context.startActivity(intent)
        }
    }
}
