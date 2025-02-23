package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.TrainLinesRepository
import com.rats.viewModels.TrainLineDetailViewModel

class TrainLineDetailViewModelFactory(private val trainLinesRepository: TrainLinesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainLineDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainLineDetailViewModel(trainLinesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
