package com.rats.data.dao

import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import org.json.JSONObject

interface ReportDao {
    suspend fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    )
}

class ReportDaoImpl(private val apiClient: ApiClient) : ReportDao {
    private val token = TokenManager.getToken()

    override suspend fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    ) {
        val body =
            JSONObject().apply {
                put("id_user", id)
                put("title", title)
                put("description", description)
                put("report_type", reportType)
                put("latitude", latitude)
                put("longitude", longitude)
            }

        val response = apiClient.postRequest("reports", body, token)

        if (response.code != 201) {
            throw Exception("Erreur lors de l'envoi de la note")
        }
    }
}
