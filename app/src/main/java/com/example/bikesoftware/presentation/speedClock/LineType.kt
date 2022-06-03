package com.example.bikesoftware.presentation.speedClock

sealed class LineType {
    object NormalStep : LineType()
    object FiveStep : LineType()
    object TenStep : LineType()
}
