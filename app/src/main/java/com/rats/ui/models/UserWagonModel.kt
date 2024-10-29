package com.rats.ui.models

import kotlinx.serialization.Serializable

@Serializable
data class UserWagonModel(
    val id: Int,
    val name: String,
    val email: String,
)
