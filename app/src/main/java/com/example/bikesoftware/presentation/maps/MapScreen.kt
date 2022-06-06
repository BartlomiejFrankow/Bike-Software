package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ZOOM_VALUE = 19f
private const val DEFAULT_ZOOM = 10f
private const val TILT = 35f // View angle in degrees
private const val BEARING = 0f // North direction

@Composable
fun MapScreen(
    userLocation: LatLng,
    viewModel: MapViewModel = hiltViewModel()
) {

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoomPreference = ZOOM_VALUE,
                minZoomPreference = ZOOM_VALUE,
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
                zoomGesturesEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                tiltGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false
            )
        )
    }

    var isFirstZoom by remember {
        mutableStateOf(true)
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, DEFAULT_ZOOM)
        isFirstZoom = false
    }

    val scaffoldState = rememberScaffoldState()

    fun zoomWithAnimation(cameraPositionState: CameraPositionState, userLocation: LatLng) {
        viewModel.viewModelScope.launch {
            delay(2000) // TODO maps sometimes is not loading as it should this delay seems to help - INVESTIGATE!

            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(userLocation, ZOOM_VALUE, TILT, BEARING)
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
                if (!isFirstZoom) zoomWithAnimation(cameraPositionState, userLocation)
            }
        }
    }

}
