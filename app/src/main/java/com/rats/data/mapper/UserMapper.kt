package com.rats.data.mapper

import com.rats.data.dto.UserDTO
import com.rats.data.dto.UserProfileDTO
import com.rats.models.User
import com.rats.models.UserProfile

object UserMapper {
    fun UserDTO.toModel(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
        )
    }

    fun UserProfileDTO.toModel(): UserProfile {
        return UserProfile(
            id = this.id,
            name = this.name,
            email = this.email,
            rating = this.rating,
            ratingCount = this.ratingCount,
            createdAt = this.createdAt,
        )
    }
}
