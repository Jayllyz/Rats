package com.rats.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)
