package com.rats.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.RatsApp
import com.rats.data.dto.UserProfileDTO
import com.rats.factories.ProfileViewModelFactory
import com.rats.utils.TokenManager
import com.rats.viewModels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private val userProfileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((application as RatsApp).userRepository)
    }

    private inner class ProfileViewHolder {
        val userNameTextView: TextView = findViewById(R.id.tv_user_name)
        val userEmailTextView: TextView = findViewById(R.id.tv_user_email)
        val userCreatedAtTextView: TextView = findViewById(R.id.tv_user_created_at)
        val userWelcomeTextView: TextView = findViewById(R.id.tv_user_welcome)
        val ratingBar: RatingBar = findViewById(R.id.rating_bar)
        val ratingValueTextView: TextView = findViewById(R.id.tv_rating_value)
        val ratingCountTextView: TextView = findViewById(R.id.tv_rating_count)
        val logoutButton: Button = findViewById(R.id.btn_logout)
        val progressBar: ProgressBar = findViewById(R.id.profile_progress)
    }

    private lateinit var viewHolder: ProfileViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        viewHolder = ProfileViewHolder()

        setupObservers()
        setupClickListeners()
        loadUserProfile()
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { userDto ->
            if (userDto != null) {
                updateUI(userDto)
            }
        }

        userProfileViewModel.isLoading.observe(this) { isLoading ->
            viewHolder.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        userProfileViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_message)
            .setPositiveButton(R.string.logout_dialog_confirm) { _, _ ->
                performLogout()
            }
            .setNegativeButton(R.string.logout_dialog_cancel, null)
            .show()
    }

    private fun performLogout() {
        TokenManager.deleteToken()

        val intent =
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        finish()
    }

    private fun setupClickListeners() {
        viewHolder.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserProfile() {
        userProfileViewModel.getUserProfile()
    }

    @SuppressLint("DefaultLocale", "StringFormatInvalid", "WeekBasedYear")
    private fun updateUI(userDto: UserProfileDTO) {
        viewHolder.userNameTextView.text = userDto.name
        viewHolder.userEmailTextView.text = userDto.email
        viewHolder.userWelcomeTextView.text = userDto.name.replaceFirstChar(Char::uppercase)

        val inputFormat = SimpleDateFormat("DD-MM-YYYY", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        try {
            val date = inputFormat.parse(userDto.createdAt)
            val formattedDate = date?.let { outputFormat.format(it) } ?: userDto.createdAt
            viewHolder.userCreatedAtTextView.text = formattedDate
        } catch (e: Exception) {
            viewHolder.userCreatedAtTextView.text = userDto.createdAt
        }

        val rating = userDto.rating.toFloat()
        viewHolder.ratingBar.rating = rating
        viewHolder.ratingValueTextView.text = String.format("%.1f", rating)

        viewHolder.ratingCountTextView.text = resources.getQuantityString(
            R.plurals.rating_count_format,
            userDto.ratingCount,
            userDto.ratingCount
        )
    }
}