package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {

    val scaffoldState = rememberScaffoldState()
    val mapUiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }

    Scaffold(scaffoldState = scaffoldState) {
        Box {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp),
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = MapType.SATELLITE
                ),
                uiSettings = mapUiSettings
            ) {

            }
        }
    }
}
