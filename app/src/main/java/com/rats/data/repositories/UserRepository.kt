package com.rats.data.repositories

import com.rats.data.dao.UserDao
import com.rats.models.User

interface UserRepository {
    suspend fun getWagonUsers(): List<User>
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getWagonUsers(): List<User> = userDao.getNearbyUsers()
}
