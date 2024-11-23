package com.rats.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceManager
import com.rats.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var securityModeSwitch: SwitchCompat

    companion object {
        private const val PREF_NOTIFICATIONS = "pref_notifications"
        private const val PREF_SECURITY_MODE = "pref_security_mode"
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
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupListeners() {
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSettings(isChecked)
        }

        securityModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSecurityModeSettings(isChecked)
        }
    }

    private fun loadSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        notificationSwitch.isChecked = prefs.getBoolean(PREF_NOTIFICATIONS, false)
        securityModeSwitch.isChecked = prefs.getBoolean(PREF_SECURITY_MODE, false)
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
}