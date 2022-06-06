package com.example.bikesoftware.presentation.maps

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import com.example.bikesoftware.utils.TIMER_PATTERN
import javax.inject.Inject

class MapViewModel @Inject constructor() : ViewModel() {

    private var startTripTime = 0L
    private var tripTimeInSeconds = 0L

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
}
