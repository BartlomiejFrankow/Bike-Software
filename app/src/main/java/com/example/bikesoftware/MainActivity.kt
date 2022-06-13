package com.example.bikesoftware

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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
import com.example.bikesoftware.extensions.areLocationPermissionsGranted
import com.example.bikesoftware.presentation.LocationInfoDialog
import com.example.bikesoftware.presentation.maps.MapScreen
import com.example.bikesoftware.presentation.maps.TripState.STARTED
import com.example.bikesoftware.presentation.speedClock.SpeedClockScreen
import com.example.bikesoftware.ui.theme.BikeSoftwareTheme
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_PERMISSIONS
import com.example.bikesoftware.utils.LOCATION_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestForegroundPermission()

        setContent {
            BikeSoftwareTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (this@MainActivity.areLocationPermissionsGranted()) {
                            ShowUi()
                            startLocationService()
                        } else {
                            Toast.makeText(this@MainActivity, getString(R.string.no_location_permission_message), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        if (areLocationPermissionsGranted()) {
            setContent { ShowUi() }
            startLocationService()
        }
        super.onResume()
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val serviceIntent = Intent(this, ForegroundLocationService::class.java)
            startService(serviceIntent)
        }
    }

    @Composable
    private fun ShowUi() {
        MapScreen(
            onStartStopClick = { tripState ->
                if (tripState == STARTED) onTripStart()
                else onTripEnd()
            }
        )
        SpeedClockScreen()
    }

    private fun onTripStart() {
        startLocationService()
    }

    private fun onTripEnd() {
        stopService(Intent(this, ForegroundLocationService::class.java))
    }


    private fun requestForegroundPermission() = ActivityCompat.requestPermissions(this, FOREGROUND_LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (areLocationPermissionsGranted()) {
                    setContent { ShowUi() }
                } else {
                    setContent {
                        LocationInfoDialog {
                            startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)))
                        }
                    }
                }
                return
            }
        }
    }

    @Suppress("DEPRECATION")
    fun isLocationServiceRunning(): Boolean {
        for (service: ActivityManager.RunningServiceInfo in (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundLocationService::class.java.name == service.service.className) return true
        }

        return false
    }
}
