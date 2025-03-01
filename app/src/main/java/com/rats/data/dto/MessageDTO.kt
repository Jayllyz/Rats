package com.rats.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    val id: Int,
    val id_sender: Int,
    val sender_name: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
)
