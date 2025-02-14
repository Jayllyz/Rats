package com.rats.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.TrainLinesRepository
import com.rats.models.Report
import kotlinx.coroutines.launch

class TrainLineDetailViewModel(private val trainLinesRepository: TrainLinesRepository) : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _subscribed = MutableLiveData<Boolean>()
    val subscribed: LiveData<Boolean> = _subscribed

    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> = _reports

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchTrainLineById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val detail = trainLinesRepository.getTrainLineById(id)
                Log.d("wtf", "Detail: $detail")
                _title.value = detail.name
                _status.value = detail.status
                _subscribed.value = detail.subscribed
                _reports.value = detail.reports
            } catch (e: Exception) {
                Log.d("wtf", "Error: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}