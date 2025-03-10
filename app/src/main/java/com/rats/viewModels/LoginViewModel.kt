package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.data.dto.UserLoginDTO
import com.rats.data.repositories.UserRepository
import com.rats.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> = _userId

    fun userLogin(userLoginDTO: UserLoginDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userToken = userRepository.userLogin(userLoginDTO)
                TokenManager.saveToken(userToken.token)
                val user = userRepository.getUserProfile()
                _userId.value = user.id
                _success.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
