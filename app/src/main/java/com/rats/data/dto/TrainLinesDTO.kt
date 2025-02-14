package com.rats.data.dto

import com.rats.models.Report
import kotlinx.serialization.Serializable

@Serializable
data class TrainLinesDTO(
    val id: Int,
    val name: String,
    val status: String,
)

@Serializable
data class TrainLineDetailDTO(
    val id: Int,
    val name: String,
    val status: String,
    val subscribed: Boolean,
    val reports: List<Report>
)