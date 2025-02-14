package com.rats.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrainLinesDTO(
    val id: Int,
    val name: String,
    val status: String,
)
