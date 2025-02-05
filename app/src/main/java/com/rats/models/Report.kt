package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: Int,
    val title: String,
    val description: String,
    val reportType: String,
    val createdAt: String,
)
