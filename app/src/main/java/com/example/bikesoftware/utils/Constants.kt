package com.example.bikesoftware.utils

import android.Manifest

const val LOCATION_REQUEST_CODE = 100

const val TIMER_PATTERN = "%02d:%02d:%02d"

// Permissions
val FOREGROUND_LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
