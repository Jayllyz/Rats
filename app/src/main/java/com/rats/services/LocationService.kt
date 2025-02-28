package com.rats.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    private lateinit var locationCallback: LocationCallback
    private lateinit var handler: Handler
    private lateinit var locationUpdateRunnable: Runnable
    private lateinit var userRepository: UserRepository
    private val normalUpdateInterval = 15000L // 15 seconds
    private val lowBatteryUpdateInterval = 60000L // 1 min
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val hardcodedLatitude = 48.8566
    private val hardcodedLongitude = 2.3522

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "LocationService"
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler = Handler(Looper.getMainLooper())
        val app = applicationContext as RatsApp
        userRepository = app.userRepository

        setupLocationCallback()
        startForeground()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                immediateLocationReceiver,
                IntentFilter("REQUEST_IMMEDIATE_LOCATION"),
                RECEIVER_NOT_EXPORTED,
            )
        } else {
            registerReceiver(immediateLocationReceiver, IntentFilter("REQUEST_IMMEDIATE_LOCATION"))
        }

        sendImmediateLocation() // Send location immediately when app starts
        startLocationUpdates()
    }

    private fun sendImmediateLocation() {
        val userLocation = UserLocationDTO(hardcodedLatitude, hardcodedLongitude)
        sendLocationToServer(userLocation)
        sendLocationBroadcast(userLocation)
    }

    private val immediateLocationReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                if (intent?.action == "REQUEST_IMMEDIATE_LOCATION") {
                    sendImmediateLocation()
                }
            }
        }

    private fun setupLocationCallback() {
        locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val userLocation = UserLocationDTO(hardcodedLatitude, hardcodedLongitude)
                    sendLocationToServer(userLocation)
                    sendLocationBroadcast(userLocation)
                }
            }
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Background Service",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    setShowBadge(false)
                }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Location tracking")
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            stopSelf()
            return
        }

        val updateInterval = if (isBatteryLow()) lowBatteryUpdateInterval else normalUpdateInterval
        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                updateInterval,
            )
                .setMinUpdateIntervalMillis(5000)
                .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
            Log.d(TAG, "Location updates requested successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting location updates", e)
        }
    }

    private fun isBatteryLow(): Boolean {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level * 100 / scale.toFloat()
        return batteryPct <= 30
    }

    private fun sendLocationToServer(location: UserLocationDTO) {
        Log.d(TAG, "Sending location to server: lat=${location.latitude}, lon=${location.longitude}")
        serviceScope.launch {
            try {
                userRepository.updateUserLocation(location)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update user location", e)
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
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            handler.removeCallbacks(locationUpdateRunnable)
            unregisterReceiver(immediateLocationReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up service", e)
        }
        serviceScope.cancel()
    }
}
