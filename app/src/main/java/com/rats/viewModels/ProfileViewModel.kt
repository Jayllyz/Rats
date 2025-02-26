package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.dto.UserProfileDTO
import com.rats.data.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userProfile = MutableLiveData<UserProfileDTO>()
    val userProfile: LiveData<UserProfileDTO> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val profileList = userRepository.userProfile()
                if (profileList.isNotEmpty()) {
                    _userProfile.value = profileList[0]
                } else {
                    _error.value = "No profile data found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load user profile"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
