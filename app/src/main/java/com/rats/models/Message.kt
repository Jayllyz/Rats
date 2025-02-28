package com.rats.models

import kotlinx.serialization.SerialName

data class Message(
    val id: Int,
    val sender: User,
    val content: String,
    val createdAt: String,
)
