package com.rats.data.dto
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO (
    val id: Int,
    val name: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
    val token: String? = null
)