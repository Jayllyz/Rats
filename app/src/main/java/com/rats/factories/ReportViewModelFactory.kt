package com.rats.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rats.data.repositories.ReportRepository
import com.rats.viewModels.ReportViewModel

class ReportViewModelFactory(private val reportRepository: ReportRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(reportRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
