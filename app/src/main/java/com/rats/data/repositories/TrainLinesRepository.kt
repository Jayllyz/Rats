package com.rats.data.repositories

import com.rats.data.dao.TrainLinesDao
import com.rats.models.ReportNoLocation
import com.rats.models.TrainLineDetail
import com.rats.models.TrainLines

interface TrainLinesRepository {
    suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines>

    suspend fun getTrainLineById(id: Int): TrainLineDetail

    suspend fun subscribeToTrainLine(id: Int)

    suspend fun unsubscribeToTrainLine(id: Int)

    suspend fun getSubscribedReports(): List<ReportNoLocation>
}

class TrainLinesRepositoryImpl(private val trainLinesDao: TrainLinesDao) : TrainLinesRepository {
    override suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines> = trainLinesDao.getTrainLines(filter, search)

    override suspend fun getTrainLineById(id: Int): TrainLineDetail = trainLinesDao.getTrainLineById(id)

    override suspend fun subscribeToTrainLine(id: Int) = trainLinesDao.subscribeToTrainLine(id)

    override suspend fun unsubscribeToTrainLine(id: Int) = trainLinesDao.unsubscribeToTrainLine(id)

    override suspend fun getSubscribedReports(): List<ReportNoLocation> = trainLinesDao.getSubscribedReports()
}
