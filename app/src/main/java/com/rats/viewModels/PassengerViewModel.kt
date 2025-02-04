package com.rats.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.RatingRepository
import com.rats.models.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PassengerViewModel(private val ratingRepository: RatingRepository) : ViewModel() {
    private val _ratings = MutableLiveData<List<Rating>>()
    val ratings: LiveData<List<Rating>> = _ratings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchRatings(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = ratingRepository.getUserRatings(id)
                _ratings.value = users
            } catch (e: Exception) {
                Log.e("Error wagon", "Error: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
