package com.rats.data.dao

import com.rats.data.dto.UserDTO
import com.rats.data.dto.UserLocationDTO
import com.rats.data.dto.UserLoginDTO
import com.rats.data.dto.UserProfileDTO
import com.rats.data.mapper.UserMapper.toModel
import com.rats.models.User
import com.rats.models.UserProfile
import com.rats.models.UserToken
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.json.JSONObject

interface UserDao {
    suspend fun getNearbyUsers(): List<User>

    suspend fun updateUserLocation(userLocationDTO: UserLocationDTO)

    suspend fun userLogin(userLoginDTO: UserLoginDTO): UserToken

    suspend fun getUserProfile(): UserProfile
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

    override suspend fun updateUserLocation(userLocationDTO: UserLocationDTO) {
        val body =
            JSONObject()
                .put("latitude", userLocationDTO.latitude)
                .put("longitude", userLocationDTO.longitude)
        val response = apiClient.putRequest("users/position", body, token)
        if (response.code != 200) {
            throw Exception("Failed to update user location")
        }
    }

    override suspend fun userLogin(userLoginDTO: UserLoginDTO): UserToken {
        val body =
            JSONObject()
                .put("email", userLoginDTO.email)
                .put("password", userLoginDTO.password)
        val response = apiClient.postRequest("auth/login", body)
        return if (response.code == 200 && response.body != null) {
            val token = json.decodeFromJsonElement<UserToken>(response.body)
            TokenManager.saveToken(token.token)
            token
        } else {
            throw Exception("Failed to authenticate user")
        }
    }

    override suspend fun getUserProfile(): UserProfile {
        val response = apiClient.getRequest("self", token)

        return if (response.code == 200 && response.body != null) {
            val userProfileDTO = json.decodeFromJsonElement<UserProfileDTO>(response.body)
            userProfileDTO.toModel()
        } else {
            throw Exception("Failed to get user profile")
        }
    }
}
