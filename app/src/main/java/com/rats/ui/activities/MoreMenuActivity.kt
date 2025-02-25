package com.rats.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.utils.TokenManager

class MoreMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_more_menu)

        TokenManager.init(this)

        val transportLineLayout: LinearLayout = findViewById(R.id.transport_line_button)
        val settingsLayout: LinearLayout = findViewById(R.id.settings_button)
        val newsletterLayout: LinearLayout = findViewById(R.id.newsletter_button)
        val logoutLayout: LinearLayout = findViewById(R.id.logout_button)

        transportLineLayout.setOnClickListener {
            val intent = Intent(this, TrainLinesActivity::class.java)
            startActivity(intent)
        }

        settingsLayout.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        newsletterLayout.setOnClickListener {
            val intent = Intent(this, TrainLineDetailActivity::class.java)
            startActivity(intent)
        }

        logoutLayout.setOnClickListener {
            showLogoutConfirmationDialog()
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
}
