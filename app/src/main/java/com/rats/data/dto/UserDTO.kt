package com.rats.data.dto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val token: String? = null,
)

@Serializable
data class UserProfileDTO(
    val id: Int,
    val name: String,
    val email: String,
    val rating: Double,
    @SerialName("rating_count")
    val ratingCount: Int,
    @SerialName("created_at")
    val createdAt: String,
)
