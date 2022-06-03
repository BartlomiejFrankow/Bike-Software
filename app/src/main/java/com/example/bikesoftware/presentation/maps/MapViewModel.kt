package com.example.bikesoftware.presentation.maps

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MapViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(MapState())

}
