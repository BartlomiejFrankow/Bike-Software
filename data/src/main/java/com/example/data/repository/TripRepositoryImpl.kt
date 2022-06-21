package com.example.data.repository

import com.example.bikesoftware.TripDatabase
import com.example.domain.dto.Altitudes
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

    override suspend fun insertTripData(speeds: String, polylineLocations: String, altitudes: String) {
        queries.insertTripData(1, speeds, polylineLocations, altitudes)
    }

    override suspend fun getAverageSpeed(): Int {
        val speedJson = queries.getSpeed().executeAsOneOrNull()

        val json = if (speedJson.isNullOrEmpty()) "{values:[0]}" else speedJson

        val speed = Gson().fromJson(json, Speed::class.java)

        return speed.values.sum() / speed.values.size
    }

    override suspend fun getHighestSpeed(): Int {
        val speedJson = queries.getSpeed().executeAsOneOrNull()

        val json = if (speedJson.isNullOrEmpty()) "{values:[0]}" else speedJson

        val speed = Gson().fromJson(json, Speed::class.java)

        return speed.values.maxByOrNull { it }!!
    }

    override suspend fun getLowestAndHighestAltitude(): Pair<Int, Int> {
        val altitudeJson = queries.getAltitudes().executeAsOneOrNull()

        val json = if (altitudeJson.isNullOrEmpty()) "{values:[0]}" else altitudeJson

        val altitudes = Gson().fromJson(json, Altitudes::class.java)

        return Pair((altitudes.values.minByOrNull { it } ?: 0).toInt(), (altitudes.values.maxByOrNull { it } ?: 0).toInt())
    }

    override suspend fun insetTripState(tripState: Boolean) {
        if (tripState) {
            insertStateAndClearOtherData(tripState)
        } else {
            insertStateWithOtherData(tripState)
        }
    }

    private fun insertStateWithOtherData(tripState: Boolean) {
        val polyLinesJson = queries.getPolylineLocations().executeAsOneOrNull() ?: ""
        val speedsJson = queries.getSpeed().executeAsOneOrNull() ?: ""
        val altitudes = queries.getAltitudes().executeAsOneOrNull() ?: ""

        queries.insertTripState(
            id = 1,
            speeds = speedsJson,
            locations = polyLinesJson,
            altitudes = altitudes,
            isTripStarted = tripState
        )
    }

    private fun insertStateAndClearOtherData(tripState: Boolean) {
        queries.insertTripState(
            id = 1,
            speeds = "",
            locations = "",
            altitudes = "",
            isTripStarted = tripState
        )
    }

    override fun observeTripState(): Flow<Boolean?> {
        return queries.getTripState().asFlow().map { it.executeAsOneOrNull()?.isTripStarted }
    }

}
