package com.example.bikesoftware

import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.bikesoftware.UserLocationViewState.UserLocationData
import com.example.bikesoftware.extensions.areLocationPermissionsGranted
import com.example.bikesoftware.presentation.maps.MapScreen
import com.example.bikesoftware.presentation.speedClock.SpeedClockScreen
import com.example.bikesoftware.ui.theme.BikeSoftwareTheme
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_PERMISSIONS
import com.example.bikesoftware.utils.LOCATION_REQUEST_CODE
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import java.lang.ref.SoftReference

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var observeStateJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestForegroundPermission()

        setContent {
            BikeSoftwareTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (this@MainActivity.areLocationPermissionsGranted()) {
                            setMapUI()
                        } else {
                            Toast.makeText(this@MainActivity, getString(R.string.no_location_permission_message), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun setMapUI() {
        getLocation()
        showUi(UserLocationData(LatLng(50.054143, 19.935028), 0)) // default Kraków Wawel location ;)
    }

    @Composable
    private fun showUi(userLocationData: UserLocationData) {
        MapScreen(userLocationData.latLng)
        SpeedClockScreen(currentSpeed = userLocationData.speed)
    }

    override fun onStart() {
        super.onStart()
        observeViewState()
    }

    override fun onStop() {
        observeStateJob.cancel()
        super.onStop()
    }

    private fun requestForegroundPermission() = ActivityCompat.requestPermissions(this, FOREGROUND_LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)

    private fun observeViewState() {
        observeStateJob = lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect { state ->
                when (state) {
                    is UserLocationData -> setContent { showUi(state) }
                }
            }
        }
    }

    private fun getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.requestLocationUpdates(viewModel.locationRequest, viewModel.locationCallback, Looper.getMainLooper())
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (areLocationPermissionsGranted()) setContent { setMapUI() }
                return
            }
        }
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
