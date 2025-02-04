package com.rats

import android.app.Application
import com.rats.data.dao.RatingDaoImpl
import com.rats.data.dao.UserDaoImpl
import com.rats.data.repositories.RatingRepository
import com.rats.data.repositories.RatingRepositoryImpl
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
}
