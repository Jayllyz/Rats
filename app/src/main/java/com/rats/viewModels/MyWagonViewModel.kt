package com.rats.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rats.models.UserWagonModel
import com.rats.repositories.MyWagonRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class MyWagonViewModel(private val myWagonRepository: MyWagonRepository): ViewModel() {
    private val _users = MutableLiveData<List<UserWagonModel>>()
    val users: LiveData<List<UserWagonModel>> = _users

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchUsers(){
        viewModelScope.launch {
            val response = myWagonRepository.getWagonUsers()
            if (response.code == 200 && response.body != null){
                val users = parseUserWagonFromJson(response.body)
                _users.value = users
            } else {
                _error.value = "Error fetching users: ${response.code}"
            }
        }
    }

    private fun parseUserWagonFromJson(body: JsonElement): List<UserWagonModel> {
        val json = Json { ignoreUnknownKeys = true }
        val users = json.decodeFromString<List<UserWagonModel>>(body.toString())
        return users
    }
}