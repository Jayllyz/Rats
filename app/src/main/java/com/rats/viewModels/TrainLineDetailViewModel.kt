package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.TrainLinesRepository
import com.rats.models.ReportNoLocation
import kotlinx.coroutines.launch

class TrainLineDetailViewModel(private val trainLinesRepository: TrainLinesRepository) : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> = _status

    private val _subscribed = MutableLiveData<Boolean?>()
    val subscribed: LiveData<Boolean?> = _subscribed

    private val _reports = MutableLiveData<List<ReportNoLocation>>()
    val reports: LiveData<List<ReportNoLocation>> = _reports

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchTrainLineById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detail = trainLinesRepository.getTrainLineById(id)
                _title.value = detail.name
                _status.value = detail.status
                _subscribed.value = detail.subscribed
                _reports.value = detail.reports
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSubscribedReports() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _reports.value = trainLinesRepository.getSubscribedReports()
                _title.value = "Vos newsletters"
                _status.value = null
                _subscribed.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSubscription(id: Int) {
        viewModelScope.launch {
            try {
                if (_subscribed.value == true) {
                    trainLinesRepository.unsubscribeToTrainLine(id)
                } else {
                    trainLinesRepository.subscribeToTrainLine(id)
                }
                _subscribed.value = !_subscribed.value!!
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
