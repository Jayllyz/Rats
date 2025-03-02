package com.rats.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.Message
import com.rats.ui.viewHolders.MessageViewHolder
import com.rats.utils.prettyDate

class MessageAdapter(private var messages: List<Message>, private val userId: Int) : RecyclerView.Adapter<MessageViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MessageViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_self_message, parent, false)
                MessageViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message_vh, parent, false)
                MessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
    ) {
        val message = messages[position]

        if (message.id_sender == userId) {
            holder.userName.text = "You"
        } else {
            holder.userName.text = message.sender_name
        }

        holder.messageContent.text = message.content

        val formattedDate = prettyDate(message.createdAt)

        if (formattedDate != null) {
            holder.messageDate.text = formattedDate
        } else {
            holder.messageDate.text = "Date invalide"
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.id_sender == userId) {
            0
        } else {
            1
        }
    }

    fun updateMessages(newMessages: List<Message>) {
        this.messages = newMessages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
