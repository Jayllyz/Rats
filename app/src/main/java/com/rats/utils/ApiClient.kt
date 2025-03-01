package com.rats.utils

import com.rats.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class ApiResponse(val code: Int, val body: JsonElement?)

object ApiClient {
    internal var client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private const val URL_START = BuildConfig.API_BASE_URL

    fun setClient(client: OkHttpClient) {
        this.client = client
    }

    suspend fun getRequest(
        url: String,
        token: String? = null,
        queryParams: Map<String, String?>? = null,
    ): ApiResponse {
        return withContext(Dispatchers.IO) {
            val httpUrlBuilder = (URL_START + url).toHttpUrlOrNull()?.newBuilder() ?: throw IllegalArgumentException("Invalid URL")

            queryParams?.forEach { (key, value) ->
                httpUrlBuilder.addQueryParameter(key, value)
            }

            val finalUrl = httpUrlBuilder.build().toString()

            val request =
                Request.Builder()
                    .url(finalUrl)
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

    suspend fun deleteRequest(
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
                    .delete()
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
