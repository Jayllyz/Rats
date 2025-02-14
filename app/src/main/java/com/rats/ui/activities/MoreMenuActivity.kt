package com.rats.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rats.R

class MoreMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_more_menu)
        val transportLineLayout: LinearLayout = findViewById(R.id.transport_line_button)
        val settingsLayout: LinearLayout = findViewById(R.id.settings_button)

        transportLineLayout.setOnClickListener {
            val intent = Intent(this, TrainLinesActivity::class.java)
            startActivity(intent)
        }

        settingsLayout.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
