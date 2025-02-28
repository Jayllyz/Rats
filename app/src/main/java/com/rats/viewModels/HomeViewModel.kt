package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.ReportRepository
import com.rats.data.repositories.UserRepository
import com.rats.models.Report
import com.rats.models.User
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository, private val reportRepository: ReportRepository) : ViewModel() {
    private val _nearbyUsers = MutableLiveData<List<User>>()
    val nearbyUsers: LiveData<List<User>> = _nearbyUsers

    private val _nearbyReports = MutableLiveData<List<Report>>()
    val nearbyReports: LiveData<List<Report>> = _nearbyReports

    fun fetchNearbyUsers() {
        viewModelScope.launch {
            _nearbyUsers.value = userRepository.getNearbyUsers()
        }
    }

    fun fetchNearbyReports() {
        viewModelScope.launch {
            _nearbyReports.value = reportRepository.getNearbyReports()
        }
    }
}
