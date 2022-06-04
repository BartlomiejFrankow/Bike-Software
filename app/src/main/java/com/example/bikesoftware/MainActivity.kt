package com.example.bikesoftware

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.bikesoftware.extensions.areLocationPermissionsGranted
import com.example.bikesoftware.presentation.maps.MapScreen
import com.example.bikesoftware.presentation.speedClock.SpeedClockScreen
import com.example.bikesoftware.ui.theme.BikeSoftwareTheme
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_PERMISSIONS
import com.example.bikesoftware.utils.LOCATION_REQUEST_CODE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestForegroundPermission()

        setContent {
            BikeSoftwareTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (this@MainActivity.areLocationPermissionsGranted()) {
                            MapScreen()
                        } else {
                            Toast.makeText(this@MainActivity, "Location permission needed", Toast.LENGTH_LONG).show()
                        }
                        SpeedClockScreen()
                    }
                }
            }
        }
    }

    fun requestForegroundPermission() = ActivityCompat.requestPermissions(this, FOREGROUND_LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE)

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BikeSoftwareTheme {
    }
}
