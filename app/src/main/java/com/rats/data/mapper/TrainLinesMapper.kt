package com.rats.data.mapper

import com.rats.data.dto.TrainLinesDTO
import com.rats.models.TrainLines

object TrainLinesMapper {
    fun TrainLinesDTO.toModel(): TrainLines {
        return TrainLines(
            id = this.id,
            name = this.name,
            status = this.status,
        )
    }
}