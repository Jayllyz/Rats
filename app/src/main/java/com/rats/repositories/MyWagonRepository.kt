package com.rats.repositories

import com.rats.utils.ApiClient
import com.rats.utils.ApiResponse
import com.rats.utils.TokenManager

class MyWagonRepository {
    suspend fun getWagonUsers(): ApiResponse{
        return ApiClient.getRequest("users/nearby", TokenManager.getToken())
    }
}