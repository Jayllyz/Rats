package com.rats.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.MyWagonViewModelFactory
import com.rats.ui.adapters.UserWagonAdapter
import com.rats.viewModels.MyWagonViewModel

class MyWagonActivity : AppCompatActivity() {
    private val myWagonViewModel: MyWagonViewModel by viewModels {
        MyWagonViewModelFactory((application as RatsApp).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wagon)

        val recyclerView: RecyclerView = findViewById(R.id.rv_wagon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        myWagonViewModel.users.observe(this) { user ->
            recyclerView.adapter = UserWagonAdapter(user, this)
        }

        myWagonViewModel.error.observe(this) { error ->
            Log.e("wagon error", "Error: $error")
        }

        myWagonViewModel.fetchUsers()
    }
}
