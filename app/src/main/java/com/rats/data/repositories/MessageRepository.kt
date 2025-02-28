package com.rats.data.repositories

import com.rats.data.dao.MessageDao

interface MessageRepository {
    suspend fun sendMessage(
        content: String,
    )
}

class MessageRepositoryImpl(private val messageDao: MessageDao) : MessageRepository {
    override suspend fun sendMessage(content: String) = messageDao.sendMessage(content)
}
