package com.rats.data.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportDTO (
    val id: Int,
    @SerialName("id_user")
    val idUser: Int?,
    @SerialName("id_train_line")
    val idTrainLine: Int?,
    val title: String,
    val description: String,
    @SerialName("report_type")
    val reportType: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("created_at")
    val createdAt: String,
)