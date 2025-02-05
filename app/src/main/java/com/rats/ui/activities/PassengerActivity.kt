package com.rats.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.PassengerViewModelFactory
import com.rats.ui.adapters.PassengerCommentsAdapter
import com.rats.viewModels.PassengerViewModel
import kotlin.getValue

class PassengerActivity : AppCompatActivity() {
    private val passengerViewModel: PassengerViewModel by viewModels {
        PassengerViewModelFactory((application as RatsApp).ratingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passenger)

        val recyclerView: RecyclerView = findViewById(R.id.comments_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        passengerViewModel.ratings.observe(this) { ratings ->
            recyclerView.adapter = PassengerCommentsAdapter(ratings)
        }

        passengerViewModel.error.observe(this) { error ->
            Log.e("passenger error", "Error: $error")
        }

        val userId = intent.getIntExtra("id", -1)
        passengerViewModel.fetchRatings(userId)

        val name = findViewById<TextView>(R.id.tv_passenger_name)
        val rating = findViewById<TextView>(R.id.passenger_rating)

        name.text = intent.getStringExtra("name")

        passengerViewModel.stars.observe(this) { ratingVar ->
            rating.text = ratingVar.toString()
        }

        passengerViewModel.commentsNbr.observe(this) { commentsNbr ->
            val comments = findViewById<TextView>(R.id.comment_number)
            comments.text =
                buildString {
                    append(commentsNbr)
                    append(" notes")
                }
        }
    }
}
