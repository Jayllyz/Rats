package com.rats.data.mapper

import com.rats.data.dto.ReportDTO
import com.rats.data.dto.SubscribedReportDTO
import com.rats.models.Report

object ReportMapper {
    fun SubscribedReportDTO.toModel(): Report {
        return Report(
            id = this.idReport,
            title = this.title + " - " + this.lineName,
            description = this.description,
            reportType = this.reportType,
            latitude = 0.0,
            longitude = 0.0,
            createdAt = this.createdAt,
        )
    }

    fun ReportDTO.toModel(): Report {
        return Report(
            id = this.id,
            title = this.title,
            description = this.description,
            reportType = this.reportType,
            latitude = this.latitude,
            longitude = this.longitude,
            createdAt = this.createdAt,
        )
    }
}
