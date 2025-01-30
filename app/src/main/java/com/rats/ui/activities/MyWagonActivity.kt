package com.rats.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.MyWagonViewModelFactory
import com.rats.ui.adapters.UserWagonAdapter
import com.rats.viewModels.MyWagonViewModel

class MyWagonActivity: AppCompatActivity() {
    private val myWagonViewModel: MyWagonViewModel by viewModels {
        MyWagonViewModelFactory((application as RatsApp).myWagonRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wagon)

        val recyclerView: RecyclerView = findViewById(R.id.rv_wagon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myWagonViewModel.users.observe(this, Observer { user ->
            recyclerView.adapter = UserWagonAdapter(user)
        })

        myWagonViewModel.error.observe(this, Observer { error ->
            Log.d("wagon error", "Error: $error")
        })

        myWagonViewModel.fetchUsers()
    }
}