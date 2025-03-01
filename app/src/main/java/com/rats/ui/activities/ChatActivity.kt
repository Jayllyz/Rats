package com.rats.ui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.MessageViewModelFactory
import com.rats.factories.RatingViewModelFactory
import com.rats.viewModels.MessageViewModel
import com.rats.viewModels.RatingViewModel
import kotlin.getValue

class ChatActivity : AppCompatActivity() {

    private val messageViewModel: MessageViewModel by viewModels {
        MessageViewModelFactory((application as RatsApp).messageRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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
