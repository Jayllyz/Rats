package com.rats.data.dao

import com.rats.data.dto.MessageDTO
import com.rats.data.dto.RatingDTO
import com.rats.data.mapper.MessageMapper.toModel
import com.rats.data.mapper.RatingMapper.toModel
import com.rats.models.Message
import com.rats.models.Rating
import com.rats.models.User
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONObject

interface MessageDao {
    suspend fun getMessages(): List<Message>

    suspend fun sendMessage(
        content: String,
    )
}

class MessageDAOImpl(private val apiClient: ApiClient) : MessageDao {

    private val json = Json { ignoreUnknownKeys = true }
    private val token = TokenManager.getToken()

    override suspend fun getMessages(): List<Message> {
        val response = apiClient.getRequest("messages", token)
        return if (response.code == 200 && response.body != null) {
            val messagesDto = json.decodeFromJsonElement<List<MessageDTO>>(response.body)
            messagesDto.map { it.toModel() }
        } else {
            emptyList()
        }
    }

    override suspend fun sendMessage(content: String) {
        val body =
            JSONObject().apply {
                put("content", content)
            }

        val response = apiClient.postRequest("messages", body, token)

        if (response.code != 201) {
            throw Exception("Erreur lors de l'envoi du message")
        }
    }

}
