package com.rats.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.card.MaterialCardView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.ReportViewModelFactory
import com.rats.viewModels.ReportViewModel
import java.io.File

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
        val cameraTakePicture = findViewById<MaterialCardView>(R.id.camera_card_view)

        val reportTypes = listOf(getString(R.string.dangerous_individual), getString(R.string.suspect_activity), getString(R.string.dangerous_garbage), getString(R.string.other))

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
            Toast.makeText(this, getString(R.string.registered_report_message), Toast.LENGTH_SHORT).show()
            finish()
        }
        cameraTakePicture.setOnClickListener {
            if (checkPermissionsCamera()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private var photoUri: Uri? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    Toast.makeText(this, "Image saved at: $uri", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                openCamera()
            }
        }

    private fun openCamera() {
        val photoFile = createImageFile()
        val photoUri =
            FileProvider.getUriForFile(
                this,
                "${this.packageName}.fileprovider",
                photoFile,
            )
        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val storageDir = this.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir,
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
