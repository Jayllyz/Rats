package com.rats.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rats.R
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_validate)
        val errorTextView = findViewById<TextView>(R.id.login_error_text)

        loginButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    if (email.isBlank() || password.isBlank()) {
                        showError(errorTextView, getString(R.string.login_fields_required))
                        return@launch
                    }

                    loginButton.isEnabled = false
                    loginButton.text = getString(R.string.loading)

                    val body =
                        JSONObject()
                            .put("email", email)
                            .put("password", password)

                    val response = ApiClient.postRequest("auth/login", body)

                    when (response.code) {
                        200 -> {
                            val token = response.body?.jsonObject?.get("token")?.jsonPrimitive?.content
                            if (token != null) {
                                TokenManager.saveToken(token)
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                finish()
                            } else {
                                showError(errorTextView, getString(R.string.login_server_error))
                            }
                        }
                        401 -> showError(errorTextView, getString(R.string.login_incorrect_credentials))
                        404 -> showError(errorTextView, getString(R.string.login_incorrect_credentials))
                        else -> showError(errorTextView, getString(R.string.login_server_error))
                    }
                } catch (e: Exception) {
                    showError(errorTextView, getString(R.string.login_network_error))
                } finally {
                    loginButton.isEnabled = true
                    loginButton.text = getString(R.string.connexion_btn_text)
                }
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

    private fun showError(
        errorTextView: TextView,
        message: String,
    ) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }
}
