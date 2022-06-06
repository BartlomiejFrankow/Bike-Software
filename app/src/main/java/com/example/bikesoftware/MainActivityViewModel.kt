package com.example.bikesoftware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.UserLocationViewState.UserLocationData
import com.example.bikesoftware.utils.toKph
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_REQUEST_INTERVAL = 2000L
private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1000L

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableSharedFlow<UserLocationViewState>()
    val viewState = _viewState.asSharedFlow()

    var polylineLocations = mutableListOf<LatLng>()

    var speeds = mutableListOf<Int>()

    var isTripStarted = false

    var locationCallback = LocationCallbackReference(object : LocationCallback() {

        override fun onLocationAvailability(availability: LocationAvailability) {
            when {
                !availability.isLocationAvailable -> {
//                    hasLostLocationInPast = true
//                    Toast.makeText(applicationContext, getString(R.string.gps_off_info), Toast.LENGTH_LONG).show()
                }
            }
        }

        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach {
                viewModelScope.launch {
                    val speedInKph = it.speed.toKph()

                    if (isTripStarted) {
                        polylineLocations.add(LatLng(it.latitude, it.longitude))
                        speeds.add(speedInKph)
                    }

                    _viewState.emit(UserLocationData(LatLng(it.latitude, it.longitude), speedInKph))
                }
            }
        }
    })

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        interval = LOCATION_REQUEST_INTERVAL
        fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
    }

    fun clearTripData() {
        polylineLocations.clear()
        speeds.clear()
    }

}
