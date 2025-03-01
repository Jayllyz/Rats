package com.rats.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.MessageRepository
import com.rats.models.Message
import kotlinx.coroutines.launch

class MessageViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    private val _content = MutableLiveData(1)
    val content: LiveData<Int> = _content

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendMessage(content: String) {
        viewModelScope.launch {
            messageRepository.sendMessage(content)
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val messages = messageRepository.getMessages()
                _messages.value = messages
            } catch (e: Exception) {
                Log.e("Error fetchMessages", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setContent(content: Int) {
        _content.value = content
    }
}
