package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.MessageRepository
import com.rats.viewModels.MessageViewModel

class MessageViewModelFactory(private val messageRepository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
