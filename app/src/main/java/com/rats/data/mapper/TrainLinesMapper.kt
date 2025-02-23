package com.rats.data.mapper

import com.rats.data.dto.TrainLineDetailDTO
import com.rats.data.dto.TrainLinesDTO
import com.rats.models.TrainLineDetail
import com.rats.models.TrainLines

object TrainLinesMapper {
    fun TrainLinesDTO.toModel(): TrainLines {
        return TrainLines(
            id = this.id,
            name = this.name,
            status = this.status,
        )
    }

    fun TrainLineDetailDTO.toModel(): TrainLineDetail {
        return TrainLineDetail(
            id = this.id,
            name = this.name,
            status = this.status,
            subscribed = this.subscribed,
            reports = this.reports,
        )
    }
}
