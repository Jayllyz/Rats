package com.rats.data.dao

import android.util.Log
import com.rats.data.dto.UserDTO
import com.rats.data.mapper.UserMapper.toModel
import com.rats.models.User
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONObject

interface UserDao {
    suspend fun getNearbyUsers(): List<User>
    suspend fun updateUserLocation(latitude: Double, longitude: Double)
}

class UserDaoImpl(private val apiClient: ApiClient) : UserDao {
    private val json = Json { ignoreUnknownKeys = true }
    private val token = TokenManager.getToken()

    override suspend fun getNearbyUsers(): List<User> {
        val response = apiClient.getRequest("users/nearby", token)
        return if (response.code == 200 && response.body != null) {
            val userDtos = json.decodeFromJsonElement<List<UserDTO>>(response.body)
            userDtos.map { it.toModel() }
        } else {
            emptyList()
        }
    }

    override suspend fun updateUserLocation(latitude: Double, longitude: Double) {
        val body = JSONObject()
                .put("latitude", latitude)
                .put("longitude", longitude)
        Log.d("wtf", "$token")
        val response = apiClient.putRequest("users/position", body, token)
        Log.d("wtf", "${response.code}")
        if (response.code != 200) {
            throw Exception("Failed to update user location")
        }
    }
}
