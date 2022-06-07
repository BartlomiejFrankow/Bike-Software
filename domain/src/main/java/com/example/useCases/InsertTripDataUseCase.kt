package com.example.useCases

import com.example.domain.repository.TripRepository
import javax.inject.Inject

class InsertTripDataUseCase @Inject constructor(private val repository: TripRepository) {
    suspend operator fun invoke(speeds: String, polylineLocations: String) = repository.insertTripData(speeds, polylineLocations)
}
