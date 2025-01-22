package com.rats.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rats.R
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeActivity: AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val UPDATE_INTERVAL = 5000L // 5 secondes
    }

    private lateinit var mMap : GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userMarker: MarkerOptions
    private lateinit var locationUpdateRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private var firstLaunch: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        startLocationUpdates()
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande la permissions d'accès à la localisation
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // ca me met a Los Angeles mdr
//                val userLocation = LatLng(location.latitude, location.longitude)
                // Paris
                val userLocation = LatLng(48.8566, 2.3522)
                val body = JSONObject()
                    .put("latitude", userLocation.latitude)
                    .put("longitude", userLocation.longitude)

                lifecycleScope.launch {
                    try {
                        ApiClient.putRequest("users/position", body, TokenManager.getToken()!!)
                        userMarker = MarkerOptions().position(userLocation).title("You are here")
                        mMap.clear()
                        mMap.addMarker(userMarker)
                    } catch (e: Exception) {
                        Log.d("ERROR_LOCATION", "getUserLocation: ${e.message}")
                    }
                }
                if (firstLaunch) {
                    firstLaunch = false
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))
                }
            } else {
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startLocationUpdates() {
        locationUpdateRunnable = object : Runnable {
            override fun run() {
                getUserLocation()
                handler.postDelayed(this, UPDATE_INTERVAL)
            }
        }

        locationUpdateRunnable.run()
    }
}