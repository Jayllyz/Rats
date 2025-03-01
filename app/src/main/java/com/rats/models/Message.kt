package com.rats.models

data class Message(
    val id: Int,
    val id_sender: Int,
    val sender_name: String,
    val content: String,
    val createdAt: String,
)
