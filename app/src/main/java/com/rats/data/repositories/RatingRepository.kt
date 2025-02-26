package com.rats.data.repositories

import com.rats.data.dao.RatingDao
import com.rats.models.Rating

interface RatingRepository {
    suspend fun getUserRatings(id: Int): List<Rating>

    suspend fun sendRating(
        id: Int,
        comment: String,
        rating: Int,
    )
}

class RatingRepositoryImpl(private val ratingDao: RatingDao) : RatingRepository {
    override suspend fun getUserRatings(id: Int): List<Rating> = ratingDao.getUserRatings(id)

    override suspend fun sendRating(
        id: Int,
        comment: String,
        rating: Int,
    ) = ratingDao.sendRating(id, comment, rating)
}
