package com.rats.data.repositories

import com.rats.data.dao.UserDao
import com.rats.data.dto.UserLocationDTO
import com.rats.data.dto.UserLoginDTO
import com.rats.models.User
import com.rats.models.UserToken

interface UserRepository {
    suspend fun getWagonUsers(): List<User>

    suspend fun updateUserLocation(userLocationDTO: UserLocationDTO)

    suspend fun userLogin(userLoginDTO: UserLoginDTO): UserToken
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getWagonUsers(): List<User> = userDao.getNearbyUsers()

    override suspend fun updateUserLocation(userLocationDTO: UserLocationDTO) = userDao.updateUserLocation(userLocationDTO)

    override suspend fun userLogin(userLoginDTO: UserLoginDTO): UserToken = userDao.userLogin(userLoginDTO)
}
