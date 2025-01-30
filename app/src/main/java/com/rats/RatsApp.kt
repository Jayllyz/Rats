package com.rats

import android.app.Application
import com.rats.repositories.MyWagonRepository

class RatsApp: Application() {
    val myWagonRepository: MyWagonRepository by lazy {
        MyWagonRepository()
    }
}