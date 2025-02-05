package com.rats.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.TrainLinesRepository
import com.rats.models.TrainLines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainLinesViewModel(private val trainLinesRepository: TrainLinesRepository) : ViewModel() {
    private val _lines = MutableLiveData<List<TrainLines>>()
    val lines: LiveData<List<TrainLines>> = _lines

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchTrainLines() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lines = trainLinesRepository.getTrainLines()
                _lines.value = lines
            } catch (e: Exception) {
                Log.e("error", "Error: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
