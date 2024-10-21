package com.rats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TokenManager.init(this)
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn = findViewById<Button>(R.id.btn_validate);

        btn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val email = findViewById<EditText>(R.id.et_email).text.toString()
                    val password = findViewById<EditText>(R.id.et_password).text.toString()

                    val jsonObject = JSONObject()
                        .put("email", email)
                        .put("password", password)
                    val body = jsonObject.toString().toRequestBody("application/json".toMediaType())

                    val response = ApiClient.postRequest("auth/login", body)

                    if (response.code != 200) {
                        Toast.makeText(this@MainActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val token = response.body?.get("token")?.jsonPrimitive?.content
                    TokenManager.saveToken(token!!)

                    Toast.makeText(this@MainActivity, TokenManager.getToken(), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
