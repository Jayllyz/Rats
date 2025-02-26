package com.rats.data.dao

import com.rats.data.dto.RatingDTO
import com.rats.data.mapper.RatingMapper.toModel
import com.rats.models.Rating
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONObject

interface RatingDao {
    suspend fun getUserRatings(id: Int): List<Rating>

    suspend fun sendRating(
        id: Int,
        comment: String,
        rating: Int,
    )
}

class RatingDaoImpl(private val apiClient: ApiClient) : RatingDao {
    private val json = Json { ignoreUnknownKeys = true }
    private val token = TokenManager.getToken()

    override suspend fun getUserRatings(id: Int): List<Rating> {
        val response = apiClient.getRequest("ratings/receiver/$id", token)
        return if (response.code == 200 && response.body != null) {
            val ratingDtos = json.decodeFromJsonElement<List<RatingDTO>>(response.body)
            ratingDtos.map { it.toModel() }
        } else {
            emptyList()
        }
    }

    override suspend fun sendRating(
        id: Int,
        comment: String,
        rating: Int,
    ) {
        val body =
            JSONObject().apply {
                put("comment", comment)
                put("stars", rating)
            }

        val response = apiClient.postRequest("ratings/$id", body, token)

        if (response.code != 201) {
            throw Exception("Erreur lors de l'envoi de la note")
        }
    }
}
