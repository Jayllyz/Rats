package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val latitude: Double,
    val longitude: Double,
)