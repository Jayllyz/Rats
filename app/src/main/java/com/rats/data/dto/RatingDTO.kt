package com.rats.data.dto
import com.rats.models.UserNoLocation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingDTO(
    val id: Int,
    val receiver: UserNoLocation,
    val sender: UserNoLocation,
    val stars: Int,
    val comment: String,
    @SerialName("created_at")
    val createdAt: String,
)
