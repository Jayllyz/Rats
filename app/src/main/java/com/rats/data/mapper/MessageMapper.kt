package com.rats.data.mapper

import com.rats.data.dto.MessageDTO
import com.rats.models.Message

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
