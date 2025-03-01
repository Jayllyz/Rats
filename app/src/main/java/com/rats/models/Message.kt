package com.rats.models

import kotlinx.serialization.SerialName

data class Message(
    val id: Int,
    val id_sender: Int,
    val sender_name: String,
    val content: String,
    val createdAt: String,
)
