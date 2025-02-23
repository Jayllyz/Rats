package com.rats

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rats.ui.activities.HomeActivity
import com.rats.ui.activities.LoginActivity
import com.rats.utils.TokenManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeTokenManager()
        navigateToAppropriateScreen()
    }

    private fun initializeTokenManager() {
        try {
            TokenManager.init(applicationContext)
        } catch (_: Exception) {
            navigateToLogin()
            return
        }
    }

    private fun navigateToAppropriateScreen() {
        val intent = when (TokenManager.getToken() != null) {
            true -> Intent(this, HomeActivity::class.java)
            false -> Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}