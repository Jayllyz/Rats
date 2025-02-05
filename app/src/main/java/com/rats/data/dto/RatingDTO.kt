package com.rats.data.dto
import com.rats.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingDTO(
    val id: Int,
    val receiver: User,
    val sender: User,
    val stars: Int,
    val comment: String,
    @SerialName("created_at")
    val createdAt: String,
)
