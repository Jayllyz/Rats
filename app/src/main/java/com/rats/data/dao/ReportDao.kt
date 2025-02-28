package com.rats.data.dao

import com.rats.data.dto.ReportDTO
import com.rats.data.mapper.ReportMapper.toModel
import com.rats.models.Report
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
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

    suspend fun getNearbyReports(): List<Report>
}

class ReportDaoImpl(private val apiClient: ApiClient) : ReportDao {
    private val json = Json { ignoreUnknownKeys = true }
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

    override suspend fun getNearbyReports(): List<Report> {
        val response = apiClient.getRequest("reports/nearby", token)
        return if (response.code == 200 && response.body != null) {
            val reportDtos = json.decodeFromJsonElement<List<ReportDTO>>(response.body)
            reportDtos.map { it.toModel() }
        } else {
            emptyList()
        }
    }
}
