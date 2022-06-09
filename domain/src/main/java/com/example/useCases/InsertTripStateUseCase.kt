package com.example.useCases

import com.example.domain.repository.TripRepository
import javax.inject.Inject

class InsertTripStateUseCase @Inject constructor(private val repository: TripRepository) {
    suspend operator fun invoke(isTripStarted: Boolean) = repository.insetTripState(isTripStarted)
}
