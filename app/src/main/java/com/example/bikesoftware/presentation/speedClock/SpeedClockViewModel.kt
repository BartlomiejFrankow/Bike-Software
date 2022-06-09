package com.example.bikesoftware.presentation.speedClock

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.useCases.ObserveSpeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpeedClockViewModel @Inject constructor(
    private val observeSpeed: ObserveSpeedUseCase
) : ViewModel() {

    var currentSpeed = mutableStateOf(0)

    init {
        viewModelScope.launch {
            observeSpeed().collect { speed ->
                speed?.values?.let {
                    currentSpeed.value = it.last()
                }
            }
        }
    }

}
