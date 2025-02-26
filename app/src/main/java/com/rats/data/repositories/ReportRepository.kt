package com.rats.data.repositories

import com.rats.data.dao.ReportDao

interface ReportRepository {
    suspend fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    )
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
}
