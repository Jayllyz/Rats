package com.rats.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

data class ApiResponse(val code: Int, val body: JsonObject?)

object ApiClient {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    //NOTE Simon: A modifer avec la vraie adresse IP pour la prod
    private const val URL_START = "http://10.0.2.2:8000/"

    suspend fun getRequest(url: String, token: String? = null): ApiResponse {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(URL_START + url)
                .apply {
                    token?.let { addHeader ("Authorization", "Bearer $token") }
                }
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonObject = bodyString?.let { json.parseToJsonElement(it) }?.jsonObject
                    ApiResponse(response.code, jsonObject)
                } else {
                    ApiResponse(response.code, null)
                }
            } catch (e: IOException) {
                ApiResponse(500, null)
            }
        }
    }

    suspend fun postRequest(url: String, body: RequestBody, token: String? = null): ApiResponse {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(URL_START + url)
                .addHeader("Content-Type", "application/json")
                .apply {
                    token?.let { addHeader ("Authorization", "Bearer $token") }
                }
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonObject = bodyString?.let { json.parseToJsonElement(it) }?.jsonObject
                    ApiResponse(response.code, jsonObject)
                } else {
                    ApiResponse(response.code, null)
                }
            } catch (e: IOException) {
                ApiResponse(500, null)
            }
        }
    }
}