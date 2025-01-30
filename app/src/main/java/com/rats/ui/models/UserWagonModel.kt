package com.rats.ui.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class UserWagonModel(
    val id: Int,
    val name: String,
    val email: String,
)
