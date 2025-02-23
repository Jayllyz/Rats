package com.rats.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.TrainLinesRepository
import com.rats.models.TrainLines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TrainLinesUiState {
    data object Loading : TrainLinesUiState()

    data class Success(val lines: List<TrainLines>) : TrainLinesUiState()

    data class Error(val message: String) : TrainLinesUiState()
}

class TrainLinesViewModel(private val trainLinesRepository: TrainLinesRepository) : ViewModel() {
    private val _filter = MutableStateFlow("")
    val filter: StateFlow<String> = _filter.asStateFlow()

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _uiState = MutableStateFlow<TrainLinesUiState>(TrainLinesUiState.Loading)
    val uiState: StateFlow<TrainLinesUiState> = _uiState.asStateFlow()

    fun setFilter(filter: String) {
        _filter.value = filter
        fetchTrainLines()
    }

    fun setSearch(search: String) {
        _search.value = search
        fetchTrainLines()
    }

    fun handleSearchQueryChange(newText: String?) {
        viewModelScope.launch {
            delay(1000) // 1 seconde
            setSearch(newText ?: "")
        }
    }

    fun fetchTrainLines() {
        viewModelScope.launch {
            _uiState.value = TrainLinesUiState.Loading
            try {
                val lines = trainLinesRepository.getTrainLines(_filter.value, _search.value)
                _uiState.value = TrainLinesUiState.Success(lines)
            } catch (e: Exception) {
                _uiState.value = TrainLinesUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
}
