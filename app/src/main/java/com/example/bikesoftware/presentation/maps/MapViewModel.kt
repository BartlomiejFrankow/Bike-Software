package com.example.bikesoftware.presentation.maps

import android.location.Location
import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikesoftware.presentation.maps.Altitude.*
import com.example.bikesoftware.presentation.maps.TripState.BEGINNING
import com.example.bikesoftware.utils.TIMER_PATTERN
import com.example.useCases.*
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
    private val isTripStartedUseCase: InsertTripStateUseCase,
    private val getHighestSpeedUseCase: GetHighestSpeedUseCase,
    private val getLowestAndHighestAltitudeUseCase: GetLowestAndHighestAltitudeUseCase,
) : ViewModel() {

    private var startTripTime = 0L
    private var tripTimeInSeconds = 0L

    val polylineLocations = mutableStateOf(listOf(LatLng(0.0, 0.0)))

    var tripState = mutableStateOf(BEGINNING)

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

    fun getTripDistance(): Pair<Int, Double> {
        var totalDistanceInMeters = 0.0

        if (polylineLocations.value.size > 1) {
            polylineLocations.value.forEachIndexed { index, _ ->
                if (index + 1 < polylineLocations.value.size) {
                    val start = polylineLocations.value[index]
                    val end = polylineLocations.value[index + 1]

                    val startPoint = Location("startPoint").apply {
                        latitude = start.latitude
                        longitude = start.longitude
                    }

                    val endPoint = Location("endPoint").apply {
                        latitude = end.latitude
                        longitude = end.longitude
                    }

                    totalDistanceInMeters += startPoint.distanceTo(endPoint)
                }
            }
        }

        val kilometers = (totalDistanceInMeters / 1000).toInt()

        return Pair(kilometers, getRemainingMeters(totalDistanceInMeters, kilometers))
    }

    private fun getRemainingMeters(totalDistanceInMeters: Double, kilometers: Int) = totalDistanceInMeters - kilometers * 1000

    fun getAverageSpeed(): Int {
        var speed = 0
        viewModelScope.launch { speed = getAverageSpeedUseCase() }
        return speed
    }

    fun getHighestSpeed(): Int {
        var speed = 0
        viewModelScope.launch { speed = getHighestSpeedUseCase() }
        return speed
    }

    fun getAltitude(altitudeType: Altitude): Int {
        var altitudes = Pair(0, 0)

        when (altitudeType) {
            HIGHEST -> if (altitudes.second != 0) return altitudes.second
            LOWEST -> if (altitudes.first != 0) return altitudes.first
        }

        viewModelScope.launch {
            altitudes = Pair(getLowestAndHighestAltitudeUseCase().first, getLowestAndHighestAltitudeUseCase().second)
        }

        return if (altitudeType == HIGHEST) altitudes.second else altitudes.first
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
    BEGINNING,
    STARTED,
    FINISHED
}

enum class Altitude {
    HIGHEST,
    LOWEST
}
