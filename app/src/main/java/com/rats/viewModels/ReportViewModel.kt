package com.rats.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.ReportRepository
import kotlinx.coroutines.launch

class ReportViewModel(private val reportRepository: ReportRepository) : ViewModel() {
    fun sendReport(
        id: Int,
        title: String,
        description: String,
        reportType: String,
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch {
            reportRepository.sendReport(id, title, description, reportType, latitude, longitude)
        }
    }
}
