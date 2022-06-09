package com.example.bikesoftware.presentation.maps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
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

    var isTripStarted by remember {
        mutableStateOf(false)
    }

    var showTripTimeSummary by remember {
        mutableStateOf(false)
    }

    fun getZoom() = if (isTripStarted) CLOSE_ZOOM else FAR_AWAY_ZOOM

    fun getCurrentLocation() = viewModel.polylineLocations.value.last()

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(getCurrentLocation(), getZoom())
        isFirstZoom = false
    }

    val scaffoldState = rememberScaffoldState()

    fun zoomWithAnimation(cameraPositionState: CameraPositionState) {
        viewModel.viewModelScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(getCurrentLocation(), getZoom(), TILT, BEARING)
                )
            )
        }
    }

    fun setTimer(isTripStarted: Boolean) {
        showTripTimeSummary = if (isTripStarted) {
            viewModel.startTimer()
            false
        } else {
            viewModel.stopTimer()
            true
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
                if (!isFirstZoom) zoomWithAnimation(cameraPositionState)

                DrawBikePath(viewModel.polylineLocations.value)
            }

            Button(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 256.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = if (isTripStarted) Color.Red else Color.Green),
                onClick = {
                    isTripStarted = !isTripStarted

                    if (isTripStarted) viewModel.clearTripData()

                    onStartStopClick(isTripStarted)

                    viewModel.setStartStopTripState(isTripStarted)

                    setTimer(isTripStarted)
                }
            ) {
                Text(
                    text = if (isTripStarted) stringResource(R.string.stop_trip) else stringResource(R.string.start_trip),
                    color = Color.White
                )
            }

            AnimatedVisibility(
                visible = showTripTimeSummary,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SummaryScreen(viewModel, viewModel.getAverageSpeed()) // TODO get speeds from view state
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
