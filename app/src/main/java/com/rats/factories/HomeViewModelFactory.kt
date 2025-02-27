package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.ReportRepository
import com.rats.data.repositories.UserRepository
import com.rats.viewModels.HomeViewModel

class HomeViewModelFactory(private val userRepository: UserRepository, private val reportRepository: ReportRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(userRepository, reportRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}