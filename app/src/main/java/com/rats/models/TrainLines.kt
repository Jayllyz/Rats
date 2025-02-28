package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class TrainLines(
    val id: Int,
    val name: String,
    val status: String,
)

@Serializable
data class TrainLineDetail(
    val id: Int,
    val name: String,
    val status: String,
    val subscribed: Boolean,
    val reports: List<ReportNoLocation>,
)
