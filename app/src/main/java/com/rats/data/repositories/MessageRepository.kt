package com.rats.data.repositories

import com.rats.data.dao.MessageDao
import com.rats.models.Message
import com.rats.models.Rating

interface MessageRepository {
    suspend fun sendMessage(
        content: String,
    )

    suspend fun getMessages(): List<Message>
}

class MessageRepositoryImpl(private val messageDao: MessageDao) : MessageRepository {
    override suspend fun sendMessage(content: String) = messageDao.sendMessage(content)
    override suspend fun getMessages(): List<Message> = messageDao.getMessages()
}
