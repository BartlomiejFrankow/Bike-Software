package com.example.useCases

import com.example.domain.repository.TripRepository
import javax.inject.Inject

class ObserveSpeedUseCase @Inject constructor(private val repository: TripRepository) {
    operator fun invoke() = repository.observeSpeed()
}
