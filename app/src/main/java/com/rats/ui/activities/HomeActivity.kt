package com.rats.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.HomeViewModelFactory
import com.rats.models.Report
import com.rats.models.User
import com.rats.services.LocationService
import com.rats.viewModels.HomeViewModel

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((application as RatsApp).userRepository, (application as RatsApp).reportRepository)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var userMarker: MarkerOptions
    private var firstLaunch: Boolean = true

    private val locationReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                if (intent?.action == "LOCATION_UPDATE") {
                    val latitude = intent.getDoubleExtra("latitude", 0.0)
                    val longitude = intent.getDoubleExtra("longitude", 0.0)
                    updateMapWithLocation(latitude, longitude)
                    homeViewModel.fetchNearbyUsers()
                    homeViewModel.fetchNearbyReports()
                }
            }
        }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                locationReceiver,
                IntentFilter("LOCATION_UPDATE"),
                RECEIVER_EXPORTED,
            )
        } else {
            registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"))
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val profileButton: ImageButton = findViewById(R.id.profile_button)
        profileButton.setOnClickListener {
            navigateToProfile()
        }

        checkLocationPermissions()

        homeViewModel.nearbyUsers.observe(this) { users ->
            updateMapWithNearbyUsers(users)
        }

        homeViewModel.nearbyReports.observe(this) { reports ->
            updateMapWithNearbyReports(reports)
        }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE,
            )
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Log.e("HomeActivity", "Refus droit location")
            }
        }
    }

    private fun startLocationService() {
        try {
            val serviceIntent = Intent(this, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Failed to start location service", e)
        }
    }

    private fun updateMapWithLocation(
        latitude: Double,
        longitude: Double,
    ) {
        if (::mMap.isInitialized) {
            mMap.clear()
            val userLocation = LatLng(latitude, longitude)
            userMarker = MarkerOptions().position(userLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mMap.addMarker(userMarker)
            if (firstLaunch) {
                mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                firstLaunch = false
            }
        }
    }

    private fun updateMapWithNearbyUsers(users: List<User>) {
        if (::mMap.isInitialized) {
            for (user in users) {
                val userLocation = LatLng(user.latitude, user.longitude)
                mMap.addMarker(MarkerOptions().position(userLocation).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
            }
        }
    }

    private fun updateMapWithNearbyReports(reports: List<Report>) {
        if (::mMap.isInitialized) {
            for (report in reports) {
                val reportLocation = LatLng(report.latitude, report.longitude)
                mMap.addMarker(MarkerOptions().position(reportLocation).title(report.reportType).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }
}
