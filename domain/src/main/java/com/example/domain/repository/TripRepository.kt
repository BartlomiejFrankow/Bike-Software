package com.example.domain.repository

import com.example.domain.dto.PolyLineLocations
import com.example.domain.dto.Speed
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    fun observePolyLineLocations(): Flow<PolyLineLocations?>

    fun observeSpeed(): Flow<Speed?>

    suspend fun insertTripData(speeds: String, polylineLocations: String, altitudes: String)

    suspend fun getAverageSpeed(): Int

    suspend fun getHighestSpeed(): Int

    suspend fun getLowestAndHighestAltitude(): Pair<Int, Int>

    suspend fun insetTripState(tripState: Boolean)

    fun observeTripState(): Flow<Boolean?>

}
