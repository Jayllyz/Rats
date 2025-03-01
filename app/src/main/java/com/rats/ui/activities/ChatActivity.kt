package com.rats.ui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.MessageViewModelFactory
import com.rats.ui.adapters.MessageAdapter
import com.rats.viewModels.MessageViewModel
import kotlin.getValue

class ChatActivity : AppCompatActivity() {
    private val messageViewModel: MessageViewModel by viewModels {
        MessageViewModelFactory((application as RatsApp).messageRepository)
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById(R.id.recycler_gchat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        messageViewModel.messages.observe(this) { messages ->
            recyclerView.adapter = MessageAdapter(messages)
        }

        messageViewModel.fetchMessages()

        val sendButton = findViewById<TextView>(R.id.button_chat_send)
        val messageContent = findViewById<TextView>(R.id.edit_gchat_message)

        sendButton.setOnClickListener {
            val comment = messageContent.text.toString()
            messageViewModel.sendMessage(comment)
            setResult(RESULT_OK)
            finish()
        }
    }
}
