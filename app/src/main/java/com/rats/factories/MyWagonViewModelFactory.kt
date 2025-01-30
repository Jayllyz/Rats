package com.rats.factories

import androidx.lifecycle.ViewModel
import com.rats.repositories.MyWagonRepository
import androidx.lifecycle.ViewModelProvider
import com.rats.viewModels.MyWagonViewModel

class MyWagonViewModelFactory(private val myWagonRepository: MyWagonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyWagonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyWagonViewModel(myWagonRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}