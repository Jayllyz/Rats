package com.rats.data.mapper

import com.rats.data.dto.ReportDTO
import com.rats.data.dto.SubscribedReportDTO
import com.rats.models.Report
import com.rats.models.ReportNoLocation

object ReportMapper {
    fun SubscribedReportDTO.toModel(): ReportNoLocation {
        return ReportNoLocation(
            id = this.idReport,
            title = this.title + " - " + this.lineName,
            description = this.description,
            reportType = this.reportType,
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
