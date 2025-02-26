package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.repositories.RatingRepository
import kotlinx.coroutines.launch

class RatingViewModel(private val ratingRepository: RatingRepository) : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _rating = MutableLiveData(1)
    val rating: LiveData<Int> = _rating

    fun sendRating(
        id: Int,
        comment: String,
        rating: Int,
    ) {
        viewModelScope.launch {
            ratingRepository.sendRating(id, comment, rating)
        }
    }

    fun setRating(rating: Int) {
        _rating.value = rating
    }
}
