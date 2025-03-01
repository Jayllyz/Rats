package com.rats.data.mapper

import com.rats.data.dto.MessageDTO
import com.rats.data.dto.RatingDTO
import com.rats.data.dto.UserDTO
import com.rats.data.mapper.UserMapper.toModel
import com.rats.models.Message
import com.rats.models.Rating

object MessageMapper {
    fun MessageDTO.toModel(): Message {
        return Message(
            id = this.id,
            id_sender = this.id_sender,
            sender_name = this.sender_name,
            content = this.content,
            createdAt = this.createdAt,
        )
    }
}