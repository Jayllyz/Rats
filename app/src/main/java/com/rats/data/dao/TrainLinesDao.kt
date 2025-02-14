package com.rats.data.dao

import android.util.Log
import com.rats.data.dto.TrainLineDetailDTO
import com.rats.data.dto.TrainLinesDTO
import com.rats.data.mapper.TrainLinesMapper.toModel
import com.rats.models.TrainLineDetail
import com.rats.models.TrainLines
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONObject

interface TrainLinesDao {
    suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines>

    suspend fun getTrainLineById(id: Int): TrainLineDetail
    suspend fun subscribeToTrainLine(id: Int)
    suspend fun unsubscribeToTrainLine(id: Int)
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

    override suspend fun getTrainLineById(id: Int): TrainLineDetail {
        val response = apiClient.getRequest("train_lines/$id", token)
        return if (response.code == 200 && response.body != null) {
            val trainLineDetailDto = json.decodeFromJsonElement<TrainLineDetailDTO>(response.body)
            trainLineDetailDto.toModel()
        } else {
            Log.e("error", "Error: ${response.code}")
            throw Exception("Veuillez vérifier votre connexion internet")
        }
    }

    override suspend fun subscribeToTrainLine(id: Int) {
        try {
            val response = apiClient.postRequest("train_lines/$id/subscribe",JSONObject(), token)
            if (response.code != 200) {
                Log.e("error", "Error: ${response.code}")
                throw Exception("Veuillez vérifier votre connexion internet")
            }
        } catch (e: Exception) {
            Log.e("error", "Error: ${e.message}")
            throw Exception("Il y a eu une erreur lors de l'abonnement")
        }
    }

    override suspend fun unsubscribeToTrainLine(id: Int) {
        try {
            val response = apiClient.deleteRequest("train_lines/$id/unsubscribe", token)
            if (response.code != 200) {
                Log.e("error", "Error: ${response.code}")
                throw Exception("Veuillez vérifier votre connexion internet")
            }
        } catch (e: Exception) {
            Log.e("error", "Error: ${e.message}")
            throw Exception("Il y a eu une erreur lors du désabonnement")
        }
    }
}
