package com.rats.models
import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Rating(
    val receiver: User,
    val sender: User,
    val stars: Int,
    val comment: String,
    val createdAt: String,
)
