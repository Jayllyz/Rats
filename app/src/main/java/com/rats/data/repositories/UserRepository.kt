package com.rats.data.repositories

import com.rats.data.dao.UserDao
import com.rats.models.User
import com.rats.models.UserToken

interface UserRepository {
    suspend fun getWagonUsers(): List<User>

    suspend fun updateUserLocation(
        latitude: Double,
        longitude: Double,
    )

    suspend fun userLogin(
        email: String,
        password: String,
    ): UserToken
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getWagonUsers(): List<User> = userDao.getNearbyUsers()

    override suspend fun updateUserLocation(
        latitude: Double,
        longitude: Double,
    ) = userDao.updateUserLocation(latitude, longitude)

    override suspend fun userLogin(
        email: String,
        password: String,
    ): UserToken = userDao.userLogin(email, password)
}
