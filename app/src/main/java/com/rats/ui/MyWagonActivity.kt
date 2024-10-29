package com.rats.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.ui.adapters.UserWagonAdapter
import com.rats.ui.models.UserWagonModel
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MyWagonActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wagon)

        val recyclerView: RecyclerView = findViewById(R.id.rv_wagon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch{
            val usersJson = ApiClient.getRequest("users/nearby", TokenManager.getToken())
            val body = usersJson.body
            val json = Json { ignoreUnknownKeys = true }
            val users = json.decodeFromString<List<UserWagonModel>>(body.toString())
            recyclerView.adapter = UserWagonAdapter(users)
        }
    }
}