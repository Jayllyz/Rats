package com.rats.data.dao

import android.util.Log
import com.rats.data.dto.UserDTO
import com.rats.data.mapper.UserMapper.toModel
import com.rats.models.User
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

interface UserDao {
    suspend fun getNearbyUsers(): List<User>
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
}