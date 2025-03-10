package com.rats.data.dto

import com.rats.models.ReportNoLocation
import kotlinx.serialization.Serializable

@Serializable
data class TrainLineDetailDTO(
    val id: Int,
    val name: String,
    val status: String,
    val subscribed: Boolean,
    val reports: List<ReportNoLocation>,
)
