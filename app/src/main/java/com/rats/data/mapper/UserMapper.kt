package com.rats.data.mapper

import com.rats.data.dto.UserDTO
import com.rats.models.User

object UserMapper {
    fun UserDTO.toModel(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email
        )
    }
}