package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class TrainLines(
    val id: Int,
    val name: String,
    val status: String,
)
