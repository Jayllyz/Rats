package com.rats.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribedReportDTO(
    @SerialName("id_line")
    val idLine: Int,
    @SerialName("line_name")
    val lineName: String,
    @SerialName("id_report")
    val idReport: Int,
    val title: String,
    val description: String,
    @SerialName("report_type")
    val reportType: String,
    @SerialName("created_at")
    val createdAt: String,
)
