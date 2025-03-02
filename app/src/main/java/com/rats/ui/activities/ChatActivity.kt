package com.rats.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
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
    private var isUserAtBottom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById(R.id.recycler_gchat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val messageAdapter = MessageAdapter(emptyList(), getUserId())
        recyclerView.adapter = messageAdapter

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        messageViewModel.messages.observe(this) { messages ->
            recyclerView.post {
                messageAdapter.updateMessages(messages)
                if (messages.isNotEmpty() && isUserAtBottom) {
                    recyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    isUserAtBottom = lastVisiblePosition == totalItemCount - 1
                }
            },
        )

        messageViewModel.fetchMessages()

        val sendButton = findViewById<TextView>(R.id.button_chat_send)
        sendButton.isEnabled = false
        val baseSendButtonColor = sendButton.currentTextColor
        sendButton.setTextColor(Color.LTGRAY)
        val messageContent = findViewById<TextView>(R.id.edit_gchat_message)

        sendButton.setOnClickListener {
            val comment = messageContent.text.toString()
            if (comment.isNotEmpty()) {
                messageViewModel.sendMessage(comment)
                messageContent.text = ""
                messageViewModel.fetchMessages()
            }
        }

        messageContent.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    sendButton.isEnabled = !p0.isNullOrEmpty()
                    if (sendButton.isEnabled) {
                        sendButton.setTextColor(baseSendButtonColor)
                    } else {
                        sendButton.setTextColor(Color.LTGRAY)
                    }
                }
            },
        )
    }

    private fun getUserId(): Int {
        val shared = this.applicationContext.getSharedPreferences("app", MODE_PRIVATE)
        val value = shared.getInt("user_id", -1)
        return value
    }
}
