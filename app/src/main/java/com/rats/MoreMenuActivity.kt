package com.rats

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MoreMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_more_menu)
        val transportLineLayout: LinearLayout = findViewById<LinearLayout>(R.id.transport_line_button)

        transportLineLayout.setOnClickListener {
            val intent = Intent(this, TransportLinesActivity::class.java)
            startActivity(intent)
        }
    }
}
