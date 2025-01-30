package com.rats.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.rats.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var securityModeSwitch: SwitchCompat
    private lateinit var darkModeSwitch: SwitchCompat

    companion object {
        private const val PREF_NOTIFICATIONS = "pref_notifications"
        private const val PREF_SECURITY_MODE = "pref_security_mode"
        private const val ENABLE_DARK_MODE = "enable_dark_mode"
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
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
    }

    private fun loadSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isSystemInDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        notificationSwitch.isChecked = prefs.getBoolean(PREF_NOTIFICATIONS, false)
        securityModeSwitch.isChecked = prefs.getBoolean(PREF_SECURITY_MODE, false)
        darkModeSwitch.isChecked = prefs.getBoolean(ENABLE_DARK_MODE, isSystemInDarkMode)
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
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            },
        )
    }
}
