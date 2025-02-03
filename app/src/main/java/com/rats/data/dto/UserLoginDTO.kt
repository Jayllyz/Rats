package com.rats.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDTO(
    val email: String,
    val password: String,
)
