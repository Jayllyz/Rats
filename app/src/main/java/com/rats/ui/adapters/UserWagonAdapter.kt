package com.rats.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.User
import com.rats.ui.viewHolders.UserWagonViewHolder

class UserWagonAdapter(private val users: List<User>): RecyclerView.Adapter<UserWagonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserWagonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wagon_view_holder, parent, false)
        return UserWagonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(
        holder: UserWagonViewHolder,
        position: Int,
    ) {
        val user = users[position]
        holder.name.text = user.name
        holder.email.text = user.email
    }
}
