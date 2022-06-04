package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.presentation.maps.AnimationType.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FIRST_ZOOM = 12f
private const val SECOND_ZOOM = 17f
private const val THIRD_ZOOM = 19f
private const val TILT = 4f
private const val BEARING = 90f

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoomPreference = 20f,
                minZoomPreference = 5f,
                isMyLocationEnabled = true,
                mapType = MapType.SATELLITE
            )
        )
    }
    var mapUiSettings by remember {
        mutableStateOf(MapUiSettings(mapToolbarEnabled = false, zoomControlsEnabled = false))
    }

    val myLocation = LatLng(50.019511, 20.020302)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLocation, 11f)
    }

    val scaffoldState = rememberScaffoldState()

    var animationType by remember {
        mutableStateOf(FIRST)
    }

    fun setAnimationType(animation: AnimationType) {
        animationType = animation
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

            }

            Button(onClick = {
                viewModel.viewModelScope.launch {
                    zoomWithAnimation(cameraPositionState, myLocation, animationType.getZoomValue()).also {
                        setAnimationType(SECOND)
                        delay(1000)

                        zoomWithAnimation(cameraPositionState, myLocation, animationType.getZoomValue()).also {
                            setAnimationType(THIRD)
                            delay(700)

                            zoomWithAnimation(cameraPositionState, myLocation, animationType.getZoomValue()).also {
                                setAnimationType(FIRST)
                            }
                        }
                    }
                }
            }) {
                Text(text = "Go to hardcoded location")
            }
        }
    }
}

fun AnimationType.getZoomValue() = when (this) {
    FIRST -> FIRST_ZOOM
    SECOND -> SECOND_ZOOM
    THIRD -> THIRD_ZOOM
}

suspend fun zoomWithAnimation(cameraPositionState: CameraPositionState, myLocation: LatLng, zoom: Float) {
    cameraPositionState.animate(
        CameraUpdateFactory.newCameraPosition(
            CameraPosition(myLocation, zoom, TILT, BEARING)
        )
    )
}

enum class AnimationType {
    FIRST, SECOND, THIRD
}
