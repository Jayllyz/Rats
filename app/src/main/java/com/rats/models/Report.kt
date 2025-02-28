package com.rats.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("report_type")
    val reportType: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("created_at")
    val createdAt: String,
)

@Serializable
data class ReportNoLocation(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("report_type")
    val reportType: String,
    @SerialName("created_at")
    val createdAt: String,
)
