package com.rats.data.repositories

import com.rats.data.dao.TrainLinesDao
import com.rats.models.TrainLines

interface TrainLinesRepository {
    suspend fun getTrainLines(): List<TrainLines>
}

class TrainLinesRepositoryImpl(private val trainLinesDao: TrainLinesDao) : TrainLinesRepository {
    override suspend fun getTrainLines(): List<TrainLines> = trainLinesDao.getTrainLines()
}
