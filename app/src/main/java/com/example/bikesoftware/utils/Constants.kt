package com.example.bikesoftware.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

const val LOCATION_REQUEST_CODE = 100

const val FOREGROUND_SERVICE_ID = 111

const val TIMER_PATTERN = "%02d:%02d:%02d"

// Permissions
@RequiresApi(Build.VERSION_CODES.Q)
val BACKGROUND_LOCATION_PERMISSION = arrayOf(
    Manifest.permission.ACCESS_BACKGROUND_LOCATION
)

val FOREGROUND_LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

// Channels
const val FOREGROUND_LOCATION_CHANNEL = "foreground_location_service_channel"
const val FOREGROUND_NOTIFICATION = "foreground_notification"
