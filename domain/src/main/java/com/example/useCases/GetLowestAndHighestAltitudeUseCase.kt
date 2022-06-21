package com.example.useCases

import com.example.domain.repository.TripRepository
import javax.inject.Inject

class GetLowestAndHighestAltitudeUseCase @Inject constructor(private val repository: TripRepository) {
    suspend operator fun invoke() = repository.getLowestAndHighestAltitude()
}
