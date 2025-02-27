package com.rats.data.repositories

import com.rats.data.dao.ReportDao
import com.rats.models.Report

interface ReportRepository {
    suspend fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    )

    suspend fun getNearbyReports(): List<Report>
}

class ReportRepositoryImpl(private val reportDao: ReportDao) : ReportRepository {
    override suspend fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    ) = reportDao.sendReport(id, title, description, reportType, latitude, longitude)

    override suspend fun getNearbyReports(): List<Report> = reportDao.getNearbyReports()
}
