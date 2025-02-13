package com.rats.data.dao

import android.util.Log
import com.rats.data.dto.TrainLinesDTO
import com.rats.data.mapper.TrainLinesMapper.toModel
import com.rats.models.TrainLines
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

interface TrainLinesDao {
    suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines>
}

class TrainLinesDaoImpl(private val apiClient: ApiClient) : TrainLinesDao {
    private val json = Json { ignoreUnknownKeys = true }
    private val token = TokenManager.getToken()

    override suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines> {
        val queries = mutableMapOf<String, String?>()
        if (filter != "") {
            filter.let { queries["status"] = it }
        }
        if (search != "") {
            search.let { queries["search"] = it }
        }

        val response = apiClient.getRequest("train_lines", token, queries)
        return if (response.code == 200 && response.body != null) {
            val trainLineDtos = json.decodeFromJsonElement<List<TrainLinesDTO>>(response.body)
            trainLineDtos.map { it.toModel() }
        } else {
            Log.e("error", "Error: ${response.code}")
            throw Exception("Veuillez vérifier votre connexion internet")
        }
    }
}
