package com.rats.ui.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.ReportViewModelFactory
import com.rats.viewModels.ReportViewModel

class ReportActivity : AppCompatActivity() {
    private val reportViewModel: ReportViewModel by viewModels {
        ReportViewModelFactory((application as RatsApp).reportRepository)
    }

    private var latitude = 0.0
    private var longitude = 0.0

    private val locationReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                if (intent?.action == "LOCATION_UPDATE") {
                    latitude = intent.getDoubleExtra("latitude", 0.0)
                    longitude = intent.getDoubleExtra("longitude", 0.0)
                }
            }
        }

    private val reportTypes = listOf("Individu dangereux", "Activité suspecte", "Déchet dangereux", "Autre")

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                locationReceiver,
                IntentFilter("LOCATION_UPDATE"),
                RECEIVER_EXPORTED,
            )
        } else {
            registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"))
        }

        val locationIntent = Intent("REQUEST_IMMEDIATE_LOCATION")
        locationIntent.setPackage(packageName)
        sendBroadcast(locationIntent)

        val userId = intent.getIntExtra("id", -1)

        val title = findViewById<TextView>(R.id.title_edit_text)
        val description = findViewById<TextView>(R.id.description_edit_text)
        val spinner = findViewById<Spinner>(R.id.spinner_reason)
        val submitButton = findViewById<TextView>(R.id.submit_button)
        val cancelButton = findViewById<TextView>(R.id.cancel_button)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        submitButton.setOnClickListener {
            reportViewModel.sendReport(
                userId,
                title.text.toString(),
                description.text.toString(),
                spinner.selectedItem.toString(),
                latitude,
                longitude,
            )
            Toast.makeText(this, "Signalement enregistré", Toast.LENGTH_SHORT).show()
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}
