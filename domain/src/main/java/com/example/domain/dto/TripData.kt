package com.example.domain.dto

data class Speed(val values: List<Int>)

data class PolyLineLocations(val values: List<Location>)

data class Altitudes(val values: List<Double>)

data class Location(
    val latitude: Double,
    val longitude: Double
)
