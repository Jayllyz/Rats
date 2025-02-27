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

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _stars = MutableLiveData<String>()
    val stars: LiveData<String> = _stars

    private val _commentsNbr = MutableLiveData<Int>()
    val commentsNbr: LiveData<Int> = _commentsNbr

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

                if (users.isEmpty()) {
                    _stars.value = "Aucune note"
                    _commentsNbr.value = 0
                    return@launch
                }

                var sum = 0
                for (user: Rating in users) {
                    sum += user.stars
                }
                val avg = sum / users.size
                _stars.value = "Note: $avg / 5"
                _commentsNbr.value = users.size
            } catch (e: Exception) {
                Log.e("Error fetchRatings", "Error: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
