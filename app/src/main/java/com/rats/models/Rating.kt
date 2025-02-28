package com.rats.models
import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Rating(
    val receiver: UserNoLocation,
    val sender: UserNoLocation,
    val stars: Int,
    val comment: String,
    val createdAt: String,
)
