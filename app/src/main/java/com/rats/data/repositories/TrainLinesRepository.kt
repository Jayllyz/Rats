package com.rats.data.repositories

import com.rats.data.dao.TrainLinesDao
import com.rats.models.TrainLineDetail
import com.rats.models.TrainLines

interface TrainLinesRepository {
    suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines>

    suspend fun getTrainLineById(id: Int): TrainLineDetail
}

class TrainLinesRepositoryImpl(private val trainLinesDao: TrainLinesDao) : TrainLinesRepository {
    override suspend fun getTrainLines(
        filter: String?,
        search: String?,
    ): List<TrainLines> = trainLinesDao.getTrainLines(filter, search)

    override suspend fun getTrainLineById(id: Int): TrainLineDetail = trainLinesDao.getTrainLineById(id)
}
