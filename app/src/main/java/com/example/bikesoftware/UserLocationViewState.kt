package com.example.bikesoftware

import com.google.android.gms.maps.model.LatLng

sealed class UserLocationViewState {
    data class UserLocationData(
        val currentLocation: LatLng?,
        val speed: Int
    ) : UserLocationViewState()
}
