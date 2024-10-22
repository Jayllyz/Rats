package com.rats.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rats.R
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.et_email)
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_validate)

        loginButton.setOnClickListener{
            lifecycleScope.launch {
                try {
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    val jsonObject = JSONObject()
                        .put("email", email)
                        .put("password", password)
                    val body = jsonObject.toString().toRequestBody("application/json".toMediaType())

                    val response = ApiClient.postRequest("auth/login", body)

                    if (response.code != 200) {
                        Toast.makeText(this@LoginActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val token = response.body?.get("token")?.jsonPrimitive?.content
                    TokenManager.saveToken(token!!)

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}