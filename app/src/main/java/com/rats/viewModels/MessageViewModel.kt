package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.MessageRepository
import kotlinx.coroutines.launch

class MessageViewModel(private val messageRepository: MessageRepository) : ViewModel() {
    private val _content = MutableLiveData(1)
    val content: LiveData<Int> = _content

    fun sendMessage(
        content: String,
    ) {
        viewModelScope.launch {
            messageRepository.sendMessage(content)
        }
    }

    fun setContent(content: Int) {
        _content.value = content
    }
}
