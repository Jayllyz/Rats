package com.rats.ui.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.rats.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var securityModeSwitch: SwitchCompat
    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var hideUsersSwitch: SwitchCompat
    private lateinit var hideReportsSwitch: SwitchCompat

    companion object {
        private const val PREF_NOTIFICATIONS = "pref_notifications"
        private const val PREF_SECURITY_MODE = "pref_security_mode"
        private const val ENABLE_DARK_MODE = "enable_dark_mode"
        private const val HIDE_OTHER_USERS = "hide_other_users"
        private const val HIDE_REPORTS = "hide_reports"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setupListeners()
        loadSettings()
    }

    private fun initializeViews() {
        notificationSwitch = findViewById(R.id.notificationSwitch)
        securityModeSwitch = findViewById(R.id.securityModeSwitch)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        hideUsersSwitch = findViewById(R.id.hideUsersSwitch)
        hideReportsSwitch = findViewById(R.id.hideReportsSwitch)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<LinearLayout>(R.id.personalDataLayout).setOnClickListener {
        }
    }

    private fun setupListeners() {
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSettings(isChecked)
        }

        securityModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSecurityModeSettings(isChecked)
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModeSettings(isChecked)
        }

        hideUsersSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveHideUsersSettings(isChecked)
        }

        hideReportsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveHideReportsSettings(isChecked)
        }
    }

    private fun loadSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isSystemInDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        notificationSwitch.isChecked = prefs.getBoolean(PREF_NOTIFICATIONS, false)
        securityModeSwitch.isChecked = prefs.getBoolean(PREF_SECURITY_MODE, false)
        darkModeSwitch.isChecked = prefs.getBoolean(ENABLE_DARK_MODE, isSystemInDarkMode)
        hideUsersSwitch.isChecked = prefs.getBoolean(HIDE_OTHER_USERS, false)
        hideReportsSwitch.isChecked = prefs.getBoolean(HIDE_REPORTS, false)
    }

    private fun saveNotificationSettings(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(PREF_NOTIFICATIONS, enabled)
            .apply()
    }

    private fun saveSecurityModeSettings(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(PREF_SECURITY_MODE, enabled)
            .apply()
    }

    private fun saveDarkModeSettings(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(ENABLE_DARK_MODE, enabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            },
        )
    }

    private fun saveHideUsersSettings(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(HIDE_OTHER_USERS, enabled)
            .apply()
    }

    private fun saveHideReportsSettings(enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putBoolean(HIDE_REPORTS, enabled)
            .apply()
    }
}
