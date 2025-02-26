package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.RatingRepository
import com.rats.viewModels.RatingViewModel

class RatingViewModelFactory(private val ratingRepository: RatingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RatingViewModel(ratingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
