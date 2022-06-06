package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

private const val CLOSE_ZOOM = 19f
private const val FAR_AWAY_ZOOM = 13f
private const val TILT = 35f // View angle in degrees
private const val BEARING = 0f // North direction

@Composable
fun MapScreen(
    currentLocation: LatLng?,
    locations: List<LatLng>,
    onStartStopClick: (isStarted: Boolean) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoomPreference = CLOSE_ZOOM,
                minZoomPreference = FAR_AWAY_ZOOM,
                isMyLocationEnabled = true,
                mapType = MapType.SATELLITE,
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                tiltGesturesEnabled = false,
                zoomGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false
            )
        )
    }

    var isFirstZoom by remember {
        mutableStateOf(true)
    }

    var isRideStarted by remember {
        mutableStateOf(false)
    }

    fun getZoom() = if (isRideStarted) CLOSE_ZOOM else FAR_AWAY_ZOOM

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        currentLocation?.let {
            position = CameraPosition.fromLatLngZoom(it, getZoom())
            isFirstZoom = false
        }
    }

    val scaffoldState = rememberScaffoldState()

    fun zoomWithAnimation(cameraPositionState: CameraPositionState, userLocation: LatLng) {
        viewModel.viewModelScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(userLocation, getZoom(), TILT, BEARING)
                )
            )
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        Box {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState
            ) {
                if (!isFirstZoom && currentLocation != null) zoomWithAnimation(cameraPositionState, currentLocation)

                DrawBikePath(locations)
            }

            Button(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 256.dp),
                onClick = {
                    isRideStarted = !isRideStarted
                    onStartStopClick(isRideStarted)
                }
            ) {
                Text(text = if (isRideStarted) stringResource(R.string.stop_ride) else stringResource(R.string.start_ride))
            }
        }
    }
}

@Composable
private fun DrawBikePath(locations: List<LatLng>) {
    repeat(locations.size) {
        Polyline(
            points = locations,
            color = Color.Green
        )
    }
}
