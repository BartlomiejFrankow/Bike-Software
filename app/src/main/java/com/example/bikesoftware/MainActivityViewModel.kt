package com.example.bikesoftware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.UserLocationViewState.*
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
import kotlin.math.roundToInt

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _viewState = MutableSharedFlow<UserLocationViewState>()
    val viewState = _viewState.asSharedFlow()

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
                    _viewState.emit(UserLocationData(LatLng(it.latitude, it.longitude), it.speed.roundToInt()))
                }
            }
        }
    })

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 0
        fastestInterval = 0
    }

}
