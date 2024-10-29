package com.rats.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.ui.models.UserWagonModel
import com.rats.ui.views.UserWagonViewHolder

class UserWagonAdapter(private val users: List<UserWagonModel>): RecyclerView.Adapter<UserWagonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserWagonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wagon_user, parent, false)
        return UserWagonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserWagonViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.email.text = user.email
    }
}