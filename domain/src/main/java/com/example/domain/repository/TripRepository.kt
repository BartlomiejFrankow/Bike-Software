package com.example.domain.repository

import com.example.domain.dto.PolyLineLocations
import com.example.domain.dto.Speed
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    fun observePolyLineLocations(): Flow<PolyLineLocations?>

    fun observeSpeed(): Flow<Speed?>

    suspend fun deleteTripData()

    suspend fun insertTripData(speeds: String, polylineLocations: String)

    suspend fun getAverageSpeed(): Int

    suspend fun insetTripState(isTripStarted: Boolean)

    fun observeTripState(): Flow<Boolean?>

}
