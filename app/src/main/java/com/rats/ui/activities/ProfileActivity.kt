package com.rats.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.ProfileViewModelFactory
import com.rats.models.UserProfile
import com.rats.utils.TokenManager
import com.rats.viewModels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    private val userProfileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((application as RatsApp).userRepository)
    }

    private inner class ProfileViewHolder {
        val backButton: ImageButton = findViewById(R.id.btn_back)
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

        setupBackNavigation()
        setupObservers()
        setupClickListeners()
        loadUserProfile()
    }

    private fun setupBackNavigation() {
        viewHolder.backButton.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            },
        )
    }

    private fun setupObservers() {
        userProfileViewModel.userProfile.observe(this) { userProfile ->
            userProfile?.let {
                updateUI(it)
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
        userProfileViewModel.fetchUserProfile()
    }

    @SuppressLint("DefaultLocale", "StringFormatInvalid", "WeekBasedYear", "SetTextI18n")
    private fun updateUI(userProfile: UserProfile) {
        viewHolder.userEmailTextView.text = userProfile.email
        viewHolder.userWelcomeTextView.text =
            buildString {
                append("Bienvenue, ")
                append(userProfile.name.replaceFirstChar(Char::uppercase))
            }

        try {
            val datePart = userProfile.createdAt.substring(0, 10)
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = simpleFormat.parse(datePart)

            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.FRENCH)
            val formattedDate = date?.let { outputFormat.format(it) }

            if (formattedDate != null) {
                viewHolder.userCreatedAtTextView.text = formattedDate
            } else {
                viewHolder.userCreatedAtTextView.text = getString(R.string.invalid_date)
            }
        } catch (_: Exception) {
            viewHolder.userCreatedAtTextView.text = getString(R.string.format_error)
        }

        val rating = userProfile.rating.toFloat()
        viewHolder.ratingBar.rating = rating
        viewHolder.ratingValueTextView.text = String.format("%.1f", rating)

        viewHolder.ratingCountTextView.text =
            resources.getQuantityString(
                R.plurals.rating_count_format,
                userProfile.ratingCount,
                userProfile.ratingCount,
            )
    }
}
