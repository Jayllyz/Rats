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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rats.R
import com.rats.services.LocationService
import com.rats.utils.ApiClient
import com.rats.utils.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var mMap: GoogleMap
    private lateinit var userMarker: MarkerOptions
    private var firstLaunch: Boolean = true
    private val token = TokenManager.getToken()

    private val locationReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                if (intent?.action == "LOCATION_UPDATE") {
                    val latitude = intent.getDoubleExtra("latitude", 0.0)
                    val longitude = intent.getDoubleExtra("longitude", 0.0)
                    Log.d("HomeActivity", "Updating map with location: $latitude, $longitude")
                    updateMapWithLocation(latitude, longitude)
                    fetchNearbyUsers()
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

        checkLocationPermissions()
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
            Log.d("HomeActivity", "Starting location service")
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
        val serviceIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun updateMapWithLocation(
        latitude: Double,
        longitude: Double,
    ) {
        if (::mMap.isInitialized) {
            mMap.clear()
            val userLocation = LatLng(latitude, longitude)
            userMarker = MarkerOptions().position(userLocation)
            mMap.addMarker(userMarker)
            if (firstLaunch) {
                mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                firstLaunch = false
            }
        }
    }

    private fun fetchNearbyUsers() {
        lifecycleScope.launch {
            val response = ApiClient.getRequest("users/nearby", token)
            if (response.code == 200) {
                response.body?.let { jsonElement ->
                    val users = parseUsersFromJson(jsonElement)
                    updateMapWithNearbyUsers(users)
                }
            } else {
                Log.e("HomeActivityCall", "Failed to fetch nearby users")
            }
        }
    }

    private fun parseUsersFromJson(jsonElement: JsonElement): List<User> {
        val users = mutableListOf<User>()
        val jsonArray = jsonElement.jsonArray
        for (item in jsonArray) {
            val jsonObject = item.jsonObject
            val id = jsonObject["id"]?.jsonPrimitive?.content ?: ""
            val name = jsonObject["name"]?.jsonPrimitive?.content ?: ""
            val userLatitude = jsonObject["latitude"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            val userLongitude = jsonObject["longitude"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            users.add(User(id, userLatitude, userLongitude, name))
        }
        return users
    }

    private fun updateMapWithNearbyUsers(users: List<User>) {
        if (::mMap.isInitialized) {
            for (user in users) {
                val userLocation = LatLng(user.latitude, user.longitude)
                mMap.addMarker(MarkerOptions().position(userLocation).title(user.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }

    data class User(val id: String, val latitude: Double, val longitude: Double, val name: String)
}
