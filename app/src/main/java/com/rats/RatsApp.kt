package com.rats

import android.app.Application
import com.rats.data.dao.MessageDAOImpl
import com.rats.data.dao.RatingDaoImpl
import com.rats.data.dao.ReportDaoImpl
import com.rats.data.dao.TrainLinesDaoImpl
import com.rats.data.dao.UserDaoImpl
import com.rats.data.repositories.MessageRepository
import com.rats.data.repositories.MessageRepositoryImpl
import com.rats.data.repositories.RatingRepository
import com.rats.data.repositories.RatingRepositoryImpl
import com.rats.data.repositories.ReportRepository
import com.rats.data.repositories.ReportRepositoryImpl
import com.rats.data.repositories.TrainLinesRepository
import com.rats.data.repositories.TrainLinesRepositoryImpl
import com.rats.data.repositories.UserRepository
import com.rats.data.repositories.UserRepositoryImpl
import com.rats.utils.ApiClient

class RatsApp : Application() {
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            UserDaoImpl(ApiClient),
        )
    }

    val ratingRepository: RatingRepository by lazy {
        RatingRepositoryImpl(
            RatingDaoImpl(ApiClient),
        )
    }

    val trainLinesRepository: TrainLinesRepository by lazy {
        TrainLinesRepositoryImpl(
            TrainLinesDaoImpl(ApiClient),
        )
    }

    val reportRepository: ReportRepository by lazy {
        ReportRepositoryImpl(
            ReportDaoImpl(ApiClient),
        )
    }

    val messageRepository: MessageRepository by lazy {
        MessageRepositoryImpl(
            MessageDAOImpl(ApiClient),
        )
    }
}
