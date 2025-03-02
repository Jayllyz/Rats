package com.rats.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.MessageRepository
import com.rats.models.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    private val _content = MutableLiveData(1)
    val content: LiveData<Int> = _content

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    init {
        startPeriodicMessageFetch()
    }

    private fun startPeriodicMessageFetch() {
        viewModelScope.launch {
            while (true) {
                fetchMessages()
                delay(5000)
            }
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            try {
                messageRepository.sendMessage(content)
            } catch (e: Exception) {
                Log.e("Error sendMessage", "Error: ${e.message}")
            } finally {
            }
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            try {
                val messages = messageRepository.getMessages()
                _messages.value = messages
                _error.value = false
            } catch (e: Exception) {
                _error.value = true
                Log.e("Error fetchMessages", "Error: ${e.message}")
            } finally {
            }
        }
    }
}
