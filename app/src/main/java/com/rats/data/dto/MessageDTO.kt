package com.rats.data.dto

import com.rats.models.User
import com.rats.models.UserNoLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO   (
    val id: Int,
    val sender: User,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
)
