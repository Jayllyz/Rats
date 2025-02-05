package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.TrainLinesRepository
import com.rats.viewModels.TrainLinesViewModel

class TrainLinesViewModelFactory(private val trainLinesRepository: TrainLinesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainLinesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainLinesViewModel(trainLinesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
