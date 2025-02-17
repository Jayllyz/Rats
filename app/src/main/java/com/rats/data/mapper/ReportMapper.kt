package com.rats.data.mapper

import com.rats.data.dto.SubscribedReportDTO
import com.rats.models.Report

object ReportMapper {
    fun SubscribedReportDTO.toModel(): Report {
        return Report(
            id = this.idReport,
            title = this.title + " - " + this.lineName,
            description = this.description,
            reportType = this.reportType,
            createdAt = this.createdAt,
        )
    }
}
