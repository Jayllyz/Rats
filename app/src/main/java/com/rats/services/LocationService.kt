package com.rats.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rats.RatsApp
import com.rats.data.dto.UserLocationDTO
import com.rats.data.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var handler: Handler
    private lateinit var locationUpdateRunnable: Runnable
    private lateinit var userRepository: UserRepository
    private val normalUpdateInterval = 5000L // 5 seconds
    private val lowBatteryUpdateInterval = 15000L // 15 seconds
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler = Handler(Looper.getMainLooper())
        val app = applicationContext as RatsApp
        userRepository = app.userRepository

        startLocationUpdates()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        locationUpdateRunnable =
            object : Runnable {
                override fun run() {
                    fetchUserLocation()
                    val interval = if (isBatteryLow()) lowBatteryUpdateInterval else normalUpdateInterval
                    handler.postDelayed(this, interval)
                }
            }
        handler.post(locationUpdateRunnable)
    }

    private fun isBatteryLow(): Boolean {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()
        return batteryPct <= 30
    }

    private fun fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                // Paris
                val userLocation = UserLocationDTO(48.8566, 2.3522)
                // val userLocation = UserLocationDTO(it.latitude, it.longitude)
                sendLocationToServer(userLocation)
                sendLocationBroadcast(userLocation)
            }
        }
    }

    private fun sendLocationToServer(location: UserLocationDTO) {
        serviceScope.launch {
            try {
                val userLocation = UserLocationDTO(location.latitude, location.longitude)
                userRepository.updateUserLocation(userLocation)
            } catch (e: Exception) {
                Log.e("LocationService", "Failed to update user location", e)
            }
        }
    }

    private fun sendLocationBroadcast(location: UserLocationDTO) {
        val intent =
            Intent("LOCATION_UPDATE").apply {
                putExtra("latitude", location.latitude)
                putExtra("longitude", location.longitude)
            }
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        handler.removeCallbacks(locationUpdateRunnable)
    }
}
