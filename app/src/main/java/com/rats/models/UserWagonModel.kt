package com.rats.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
data class UserWagonModel(
    val id: Int,
    val name: String,
    val email: String,
)