package com.example.useCases

import com.example.domain.repository.TripRepository
import javax.inject.Inject

class ObserveTripStateUseCase @Inject constructor(private val repository: TripRepository) {
    operator fun invoke() = repository.observeTripState()
}
