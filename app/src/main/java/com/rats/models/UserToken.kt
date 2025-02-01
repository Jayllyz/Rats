package com.rats.models

import kotlinx.serialization.Serializable

@Serializable
data class UserToken(
    val token: String,
)
