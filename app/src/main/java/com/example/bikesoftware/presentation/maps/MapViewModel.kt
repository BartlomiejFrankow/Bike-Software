package com.example.bikesoftware.presentation.maps

import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.presentation.maps.TripState.BEFORE_START
import com.example.bikesoftware.utils.TIMER_PATTERN
import com.example.useCases.GetAverageSpeedUseCase
import com.example.useCases.InsertTripStateUseCase
import com.example.useCases.ObservePolyLineUseCase
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val observePolyLinesUseCase: ObservePolyLineUseCase,
    private val getAverageSpeedUseCase: GetAverageSpeedUseCase,
    private val isTripStartedUseCase: InsertTripStateUseCase
) : ViewModel() {

    private var startTripTime = 0L
    private var tripTimeInSeconds = 0L

    val polylineLocations = mutableStateOf(listOf(LatLng(0.0, 0.0)))

    var tripState = mutableStateOf(BEFORE_START)

    init {
        observeTripData()
    }

    fun startTimer() {
        startTripTime = SystemClock.elapsedRealtime()
    }

    fun stopTimer() {
        tripTimeInSeconds = (SystemClock.elapsedRealtime() - startTripTime) / 1000
    }

    fun getTripTimeSummary(): String {
        val hours = tripTimeInSeconds / 3600
        val minutes = (tripTimeInSeconds % 3600) / 60
        val seconds = tripTimeInSeconds % 60

        return String.format(TIMER_PATTERN, hours, minutes, seconds)
    }

    private fun observeTripData() {
        viewModelScope.launch {
            observePolyLinesUseCase().collect { polyLineLocations ->
                polyLineLocations?.let { polyLines ->
                    polylineLocations.value = polyLines.values.map { LatLng(it.latitude, it.longitude) }
                }
            }
        }
    }

    fun setTripStateInDatabase(isTripStarted: Boolean) {
        viewModelScope.launch {
            isTripStartedUseCase(isTripStarted)
        }
    }

    fun getAverageSpeed(): Int {
        var speed = 0

        viewModelScope.launch {
            speed = getAverageSpeedUseCase()
        }

        return speed
    }

    fun getLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()

        polylineLocations.value.forEach {
            builder.include(it)
        }

        return builder.build()
    }
}

enum class TripState {
    BEFORE_START,
    STARTED,
    FINISHED
}
