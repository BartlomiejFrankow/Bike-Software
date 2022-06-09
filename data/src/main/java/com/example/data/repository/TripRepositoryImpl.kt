package com.example.data.repository

import com.example.bikesoftware.TripDatabase
import com.example.domain.dto.PolyLineLocations
import com.example.domain.dto.Speed
import com.example.domain.repository.TripRepository
import com.google.gson.Gson
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(db: TripDatabase) : TripRepository {

    private val queries = db.tripEntityQueries

    override fun observePolyLineLocations() = queries.getPolylineLocations().asFlow().map {
        Gson().fromJson(it.executeAsOneOrNull(), PolyLineLocations::class.java)
    }

    override fun observeSpeed() = queries.getSpeed().asFlow().map {
        Gson().fromJson(it.executeAsOneOrNull(), Speed::class.java)
    }

    override suspend fun deleteTripData() {
        queries.deleteTripData()
    }

    override suspend fun insertTripData(speeds: String, polylineLocations: String) {
        queries.insertTripData(1, speeds, polylineLocations)
    }

    override suspend fun getAverageSpeed(): Int {
        var averageSpeed = 0

        queries.getSpeed().executeAsOneOrNull()?.let {
            Gson().fromJson(it, Speed::class.java)?.let { speed ->
                averageSpeed = speed.values.sum() / speed.values.size
            }
        }

        return averageSpeed
    }

    override suspend fun insetTripState(isTripStarted: Boolean) {
        queries.insertTripState(isTripStarted)
    }

    override fun observeTripState(): Flow<Boolean?> {
        return queries.getTripState().asFlow().map { it.executeAsOneOrNull()?.isTripStarted }
    }

}
