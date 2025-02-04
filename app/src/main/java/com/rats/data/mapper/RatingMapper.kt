package com.rats.data.mapper

import com.rats.data.dto.RatingDTO
import com.rats.models.Rating

object RatingMapper {
    fun RatingDTO.toModel(): Rating {
        return Rating(
            receiver = this.receiver,
            sender = this.sender,
            stars = this.stars,
            comment = this.comment,
            createdAt = this.createdAt,
        )
    }
}
