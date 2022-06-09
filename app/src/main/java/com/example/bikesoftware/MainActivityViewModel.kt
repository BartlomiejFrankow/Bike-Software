package com.example.bikesoftware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.useCases.DeleteTripDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val deleteTripDataUseCase: DeleteTripDataUseCase
) : ViewModel() {

    fun clearTripData() {
        viewModelScope.launch {
            deleteTripDataUseCase()
        }
    }

}
