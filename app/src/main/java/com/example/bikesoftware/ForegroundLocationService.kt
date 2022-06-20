package com.example.bikesoftware

import android.annotation.SuppressLint
import android.app.*
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.location.Location.distanceBetween
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_CHANNEL
import com.example.bikesoftware.utils.FOREGROUND_NOTIFICATION
import com.example.bikesoftware.utils.FOREGROUND_SERVICE_ID
import com.example.bikesoftware.utils.toKph
import com.example.domain.dto.Location
import com.example.domain.dto.PolyLineLocations
import com.example.domain.dto.Speed
import com.example.useCases.InsertTripDataUseCase
import com.example.useCases.ObserveTripStateUseCase
import com.google.android.gms.location.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.lang.ref.SoftReference
import javax.inject.Inject

private const val LOCATION_REQUEST_INTERVAL = 2000L
private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1000L
private const val DISTANCE_IN_METERS = 50

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ForegroundLocationService : Service() {

    @Inject
    lateinit var insertTripDataUseCase: InsertTripDataUseCase

    @Inject
    lateinit var observeTripStateUseCase: ObserveTripStateUseCase

    var polylineLocations = mutableListOf<Location>()

    var speeds = mutableListOf<Int>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var hasLostLocationInPast = false

    private var wasDataCleared = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_SERVICE_ID, showNotification())

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        getLocation()
        observeTripState()
    }

    override fun onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
        job.cancel()

        super.onDestroy()
    }

    private fun showNotification() =
        Notification.Builder(this, FOREGROUND_LOCATION_CHANNEL)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.location_is_running))
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO add app icon
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE))
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(NotificationChannel(FOREGROUND_LOCATION_CHANNEL, FOREGROUND_NOTIFICATION, IMPORTANCE_DEFAULT))
        }
    }

    private fun observeTripState() {
        scope.launch {
            observeTripStateUseCase().collect { tripState ->
                tripState?.let { isTripStarted ->
                    if (isTripStarted) {
                        if (!wasDataCleared) clearData()
                    } else {
                        wasDataCleared = false
                    }
                }
            }
        }
    }

    private fun clearData() {
        speeds = mutableListOf()
        polylineLocations = mutableListOf()
        wasDataCleared = true
    }

    private var locationCallback = LocationCallbackReference(object : LocationCallback() {

        override fun onLocationAvailability(availability: LocationAvailability) {
            when {
                !availability.isLocationAvailable -> {
                    hasLostLocationInPast = true
                    Toast.makeText(applicationContext, getString(R.string.gps_off_info), Toast.LENGTH_LONG).show()
                }
                availability.isLocationAvailable && hasLostLocationInPast -> {
                    Toast.makeText(applicationContext, getString(R.string.gps_back), Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach {
                scope.launch {
                    val speedInKph = it.speed.toKph()

                    val gson = Gson()
                    val newLocation = Location(it.latitude, it.longitude)

                    if (polylineLocations.isEmpty()
                        || areNotSameLocations(newLocation, polylineLocations.last())
                        && isDistanceOk(newLocation, polylineLocations.last())
                    ) {
                        polylineLocations.add(newLocation)
                        speeds.add(speedInKph)

                        insertTripDataUseCase(gson.toJson(Speed(speeds)), gson.toJson(PolyLineLocations(polylineLocations)))
                    }
                }
            }
        }
    })

    private fun isDistanceOk(newLocation: Location, lastKnownLocation: Location): Boolean {
        val distanceInMeters = FloatArray(1)
        distanceBetween(newLocation.latitude, newLocation.longitude, lastKnownLocation.latitude, lastKnownLocation.longitude, distanceInMeters)
        return distanceInMeters[0] < DISTANCE_IN_METERS
    }

    private fun areNotSameLocations(first: Location, second: Location) = first != second

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = LOCATION_REQUEST_INTERVAL
        fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
}

// SoftReference removes callback memory leak
class LocationCallbackReference(locationCallback: LocationCallback?) : LocationCallback() {
    private val mLocationCallbackRef: SoftReference<LocationCallback> = SoftReference(locationCallback)

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        mLocationCallbackRef.get()?.onLocationResult(locationResult)
    }

    override fun onLocationAvailability(locationAvailability: LocationAvailability) {
        super.onLocationAvailability(locationAvailability)
        mLocationCallbackRef.get()?.onLocationAvailability(locationAvailability)
    }
}

