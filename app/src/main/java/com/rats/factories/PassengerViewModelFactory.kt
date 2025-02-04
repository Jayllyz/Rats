package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.RatingRepository
import com.rats.viewModels.PassengerViewModel

class PassengerViewModelFactory(private val ratingRepository: RatingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PassengerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PassengerViewModel(ratingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
