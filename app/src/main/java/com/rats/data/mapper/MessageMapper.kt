package com.rats.data.mapper

import com.rats.data.dto.MessageDTO
import com.rats.data.dto.RatingDTO
import com.rats.models.Message
import com.rats.models.Rating

object MessageMapper {
    fun MessageDTO.toModel(): Message {
        return Message(
            id = this.id,
            sender = this.sender,
            content = this.content,
            createdAt = this.createdAt,
        )
    }
}