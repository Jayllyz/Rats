package com.rats.data.dao

import com.rats.data.dto.RatingDTO
import com.rats.data.mapper.RatingMapper.toModel
import com.rats.models.Rating
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

interface RatingDao {
    suspend fun getUserRatings(id: Int): List<Rating>
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
}
