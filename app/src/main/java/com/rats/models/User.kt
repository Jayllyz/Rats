package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
)

@Serializable
data class UserProfile(
    val id: Int,
    val name: String,
    val email: String,
    val rating: Double,
    val ratingCount: Int,
    val createdAt: String,
)
