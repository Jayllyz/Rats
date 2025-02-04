package com.rats.data.dto
import android.annotation.SuppressLint
import com.rats.models.User
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RatingDTO(
    val id: Int,
    val receiver: User,
    val sender: User,
    val stars: Int,
    val comment: String,
    val createdAt: String,
)
