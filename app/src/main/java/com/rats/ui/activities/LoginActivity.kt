package com.rats.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rats.R
import com.rats.RatsApp
import com.rats.data.dto.UserLoginDTO
import com.rats.factories.LoginViewModelFactory
import com.rats.viewModels.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((application as RatsApp).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_validate)
        val errorTextView = findViewById<TextView>(R.id.login_error_text)

        lifecycleScope.launch {
            try {
                loginViewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        loginButton.isEnabled = false
                        loginButton.text = getString(R.string.loading)
                    } else {
                        loginButton.isEnabled = true
                        loginButton.text = getString(R.string.connexion_btn_text)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loginViewModel.error.observe(this) { error ->
            showError(errorTextView, error)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                showError(errorTextView, getString(R.string.login_fields_required))
                return@setOnClickListener
            }

            val userLogins = UserLoginDTO(email, password)
            loginViewModel.userLogin(userLogins)
        }

        loginViewModel.success.observe(this) { success ->
            if (success) {
                loginViewModel.userId.value?.let { saveUserId(it) }
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        emailEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    errorTextView.visibility = View.INVISIBLE
                }
            },
        )

        passwordEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    errorTextView.visibility = View.INVISIBLE
                }
            },
        )
    }

    private fun saveUserId(id: Int) {
        this.applicationContext.getSharedPreferences("app", MODE_PRIVATE)
            .edit()
            .putInt("user_id", id)
            .apply()
    }

    private fun showError(
        errorTextView: TextView,
        message: String,
    ) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }
}
