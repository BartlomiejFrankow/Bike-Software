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
import com.example.bikesoftware.presentation.maps.TripState.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

private const val CLOSE_ZOOM = 19f
private const val FAR_AWAY_ZOOM = 15f
private const val TILT = 35f // View angle in degrees
private const val BEARING = 0f // North direction
private const val ZOOM_PADDING = 50

@Composable
fun MapScreen(
    onStartStopClick: (tripState: TripState) -> Unit,
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

    var showTripTimeSummary by remember {
        mutableStateOf(false)
    }

    fun getZoom() = when (viewModel.tripState.value) {
        BEFORE_START -> FAR_AWAY_ZOOM
        STARTED -> CLOSE_ZOOM
        FINISHED -> FAR_AWAY_ZOOM
    }

    fun getCurrentLocation() = viewModel.polylineLocations.value.last()

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(getCurrentLocation(), getZoom())
        isFirstZoom = false
    }

    val scaffoldState = rememberScaffoldState()

    suspend fun zoom(cameraPositionState: CameraPositionState, zoom: Float) {
        cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(CameraPosition(getCurrentLocation(), zoom, TILT, BEARING)))
    }

    suspend fun tripSummaryZoom(cameraPositionState: CameraPositionState) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(viewModel.getLatLngBounds(), ZOOM_PADDING))
    }

    fun zoomWithAnimation(cameraPositionState: CameraPositionState) {
        viewModel.viewModelScope.launch {
            when (viewModel.tripState.value) {
                BEFORE_START -> zoom(cameraPositionState, FAR_AWAY_ZOOM)
                STARTED -> zoom(cameraPositionState, CLOSE_ZOOM)
                FINISHED -> tripSummaryZoom(cameraPositionState)
            }
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

                if (viewModel.tripState.value == STARTED || viewModel.tripState.value == FINISHED) {
                    DrawBikePath(viewModel.polylineLocations.value)
                }
            }

            Button(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 256.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = if (viewModel.tripState.value == STARTED) Color.Red else Color.Green),
                onClick = {
                    viewModel.tripState.value = when (viewModel.tripState.value) {
                        BEFORE_START -> STARTED
                        STARTED -> FINISHED
                        FINISHED -> STARTED
                    }

                    viewModel.setStartStopTripState(viewModel.tripState.value == STARTED)

                    onStartStopClick(viewModel.tripState.value)

                    setTimer(viewModel.tripState.value == STARTED)
                }
            ) {
                Text(
                    text = if (viewModel.tripState.value == STARTED) stringResource(R.string.stop_trip) else stringResource(R.string.start_trip),
                    color = Color.White
                )
            }

            AnimatedVisibility(
                visible = showTripTimeSummary,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SummaryScreen(viewModel, viewModel.getAverageSpeed())
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
