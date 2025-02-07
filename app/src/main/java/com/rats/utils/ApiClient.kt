package com.rats.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class ApiResponse(val code: Int, val body: JsonElement?)

object ApiClient {
    internal var client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    // NOTE Simon: A modifer avec la vraie adresse IP pour la prod
    private const val URL_START = "http://10.0.2.2:8000/"

    fun setClient(client: OkHttpClient) {
        this.client = client
    }

    suspend fun getRequest(
        url: String,
        token: String? = null,
    ): ApiResponse {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder()
                    .url(URL_START + url)
                    .apply {
                        token?.let { addHeader("Authorization", "Bearer $token") }
                    }
                    .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonElement = bodyString?.let { json.parseToJsonElement(it) }
                    ApiResponse(response.code, jsonElement)
                } else {
                    ApiResponse(response.code, null)
                }
            } catch (_: IOException) {
                ApiResponse(500, null)
            }
        }
    }

    suspend fun postRequest(
        url: String,
        body: JSONObject,
        token: String? = null,
    ): ApiResponse {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder()
                    .url(URL_START + url)
                    .addHeader("Content-Type", "application/json")
                    .apply {
                        token?.let { addHeader("Authorization", "Bearer $token") }
                    }
                    .post(formatBody(body))
                    .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonElement = bodyString?.let { json.parseToJsonElement(it) }
                    ApiResponse(response.code, jsonElement)
                } else {
                    ApiResponse(response.code, null)
                }
            } catch (_: IOException) {
                ApiResponse(500, null)
            }
        }
    }

    suspend fun putRequest(
        url: String,
        body: JSONObject,
        token: String? = null,
    ): ApiResponse {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder()
                    .url(URL_START + url)
                    .addHeader("Content-Type", "application/json")
                    .apply {
                        token?.let { addHeader("Authorization", "Bearer $token") }
                    }
                    .put(formatBody(body))
                    .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    val jsonElement = bodyString?.let { json.parseToJsonElement(it) }
                    ApiResponse(response.code, jsonElement)
                } else {
                    ApiResponse(response.code, null)
                }
            } catch (_: IOException) {
                ApiResponse(500, null)
            }
        }
    }

    private fun formatBody(body: JSONObject): RequestBody {
        return body.toString().toRequestBody("application/json".toMediaType())
    }
}
