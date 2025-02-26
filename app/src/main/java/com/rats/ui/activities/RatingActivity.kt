package com.rats.ui.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.RatingViewModelFactory
import com.rats.viewModels.RatingViewModel

class RatingActivity : AppCompatActivity() {
    private val ratingViewModel: RatingViewModel by viewModels {
        RatingViewModelFactory((application as RatsApp).ratingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        val userId = intent.getIntExtra("id", -1)
        val name = intent.getStringExtra("name")

        val star1 = findViewById<ImageView>(R.id.star1)
        val star2 = findViewById<ImageView>(R.id.star2)
        val star3 = findViewById<ImageView>(R.id.star3)
        val star4 = findViewById<ImageView>(R.id.star4)
        val star5 = findViewById<ImageView>(R.id.star5)

        ratingViewModel.rating.observe(this) { rating ->
            val stars = listOf(star1, star2, star3, star4, star5)
            stars.forEachIndexed { index, star ->
                val drawableRes = if (index < rating) R.drawable.star_filled else R.drawable.star_empty
                star.setImageResource(drawableRes)
            }
        }

        val title = findViewById<TextView>(R.id.tv_rating_title)
        val textZone = findViewById<TextView>(R.id.comment_edit_text)
        val sendButton = findViewById<TextView>(R.id.submit_button)
        val cancelButton = findViewById<TextView>(R.id.cancel_button)

        title.text = name
        star1.setOnClickListener { ratingViewModel.setRating(1) }
        star2.setOnClickListener { ratingViewModel.setRating(2) }
        star3.setOnClickListener { ratingViewModel.setRating(3) }
        star4.setOnClickListener { ratingViewModel.setRating(4) }
        star5.setOnClickListener { ratingViewModel.setRating(5) }

        sendButton.setOnClickListener {
            val comment = textZone.text.toString()
            ratingViewModel.sendRating(userId, comment, ratingViewModel.rating.value!!)
            setResult(RESULT_OK)
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}
