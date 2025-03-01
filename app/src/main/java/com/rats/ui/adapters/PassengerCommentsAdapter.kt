package com.rats.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.Rating
import com.rats.ui.viewHolders.PassengerCommentsViewHolder

class PassengerCommentsAdapter(private val ratings: List<Rating>) : RecyclerView.Adapter<PassengerCommentsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PassengerCommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment_view_holder, parent, false)
        return PassengerCommentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ratings.size
    }

    override fun onBindViewHolder(
        holder: PassengerCommentsViewHolder,
        position: Int,
    ) {
        val rating = ratings[position]
        holder.name.text = rating.sender.name
        holder.stars.rating = rating.stars.toFloat()
        holder.comment.text = rating.comment
        holder.date.text = rating.createdAt.split("T")[0]
    }
}
