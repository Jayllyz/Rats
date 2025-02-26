package com.rats.utils

import android.content.Context
import android.content.SharedPreferences
import kotlin.time.Duration.Companion.days

object TokenManager {
    private const val PREFS_NAME = "app_prefs"
    private const val TOKEN_KEY = "token"
    private const val TOKEN_TIMESTAMP_KEY = "token_timestamp"
    private val TOKEN_EXPIRATION_DURATION = 7.days.inWholeMilliseconds

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String) {
        requireNotNull(sharedPreferences) { "TokenManager must be initialized first" }

        val now = System.currentTimeMillis()
        sharedPreferences?.edit()?.apply {
            putString(TOKEN_KEY, token)
            putLong(TOKEN_TIMESTAMP_KEY, now)
            apply()
        }
    }

    fun getToken(): String? {
        requireNotNull(sharedPreferences) { "TokenManager must be initialized first" }

        val token = sharedPreferences?.getString(TOKEN_KEY, null)
        val timestamp = sharedPreferences?.getLong(TOKEN_TIMESTAMP_KEY, 0L) ?: 0L

        if (token == null || timestamp == 0L) {
            return null
        }

        val now = System.currentTimeMillis()
        val timeDifference = now - timestamp

        return when {
            timestamp > now -> {
                deleteToken()
                null
            }
            timeDifference > TOKEN_EXPIRATION_DURATION -> {
                deleteToken()
                null
            }
            else -> token
        }
    }

    fun deleteToken() {
        requireNotNull(sharedPreferences) { "TokenManager must be initialized first" }

        sharedPreferences?.edit()?.apply {
            remove(TOKEN_KEY)
            remove(TOKEN_TIMESTAMP_KEY)
            apply()
        }
    }
}
