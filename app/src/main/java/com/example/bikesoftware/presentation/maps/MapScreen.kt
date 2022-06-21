package com.example.bikesoftware.presentation.maps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.R
import com.example.bikesoftware.presentation.maps.TripState.*
import com.example.bikesoftware.ui.theme.DirtWhite
import com.example.bikesoftware.ui.theme.NeonOrange
import com.example.bikesoftware.ui.theme.Orange
import com.example.bikesoftware.ui.theme.Red
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CLOSE_ZOOM = 18.5f // Ride zoom
private const val MID_ZOOM = 14f // Second zoom after some time
private const val FAR_AWAY_ZOOM = 9f // First zoom and limit for last summary zoom
private const val TILT = 35f // View angle in degrees
private const val BEARING = 0f // North direction
private const val ZOOM_PADDING = 50 // Trip summary map zoom padding

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

    var beginningZoom by remember {
        mutableStateOf(FAR_AWAY_ZOOM)
    }

    var showTripTimeSummary by remember {
        mutableStateOf(false)
    }

    fun getZoom() = when (viewModel.tripState.value) {
        BEGINNING -> FAR_AWAY_ZOOM
        STARTED -> CLOSE_ZOOM
        FINISHED -> FAR_AWAY_ZOOM
    }

    fun getCurrentLocation() = viewModel.polylineLocations.value.last()

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(getCurrentLocation(), getZoom())
        isFirstZoom = false
    }

    suspend fun zoom(cameraPositionState: CameraPositionState, zoom: Float) {
        cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(CameraPosition(getCurrentLocation(), zoom, TILT, BEARING)))
    }

    suspend fun tripSummaryZoom(cameraPositionState: CameraPositionState) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(viewModel.getLatLngBounds(), ZOOM_PADDING))
    }

    fun zoomWithAnimation(cameraPositionState: CameraPositionState) {
        viewModel.viewModelScope.launch {
            when (viewModel.tripState.value) {
                BEGINNING -> {
                    zoom(cameraPositionState, beginningZoom)

                    delay(2000) // one time delay for zoom animation
                    beginningZoom = MID_ZOOM
                }
                STARTED -> {
                    zoom(cameraPositionState, CLOSE_ZOOM)
                }
                FINISHED -> {
                    tripSummaryZoom(cameraPositionState)
                    beginningZoom = FAR_AWAY_ZOOM
                }
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

    Box {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = dimensionResource(R.dimen.large_padding)),
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
            .padding(
                bottom = dimensionResource(R.dimen.start_stop_bottom_padding),
                start = dimensionResource(R.dimen.mid_padding),
                end = dimensionResource(R.dimen.mid_padding)
            )
            .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (viewModel.tripState.value == STARTED) Red else Orange),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corners_round)),
            onClick = {
                setTripState(viewModel)

                viewModel.setTripStateInDatabase(viewModel.tripState.value == STARTED)

                onStartStopClick(viewModel.tripState.value)

                setTimer(viewModel.tripState.value == STARTED)
            }
        ) {
            Text(
                text = if (viewModel.tripState.value == STARTED) stringResource(R.string.stop_trip) else stringResource(R.string.start_trip),
                color = DirtWhite,
                fontSize = dimensionResource(R.dimen.mid_text).value.sp,
                fontFamily = FontFamily(Font(R.font.lexend_thin)),
                fontWeight = FontWeight.Bold
            )
        }

        AnimatedVisibility(
            visible = showTripTimeSummary,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SummaryScreen(viewModel) {
                showTripTimeSummary = false
            }
        }
    }
}

private fun setTripState(viewModel: MapViewModel) {
    viewModel.tripState.value = when (viewModel.tripState.value) {
        BEGINNING -> STARTED
        STARTED -> FINISHED
        FINISHED -> STARTED
    }
}

@Composable
private fun DrawBikePath(locations: List<LatLng>) {
    Polyline(
        points = locations,
        color = NeonOrange
    )
}
