package com.example.bikesoftware.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_PERMISSIONS

fun Context.areLocationPermissionsGranted(): Boolean {
    val permissions = FOREGROUND_LOCATION_PERMISSIONS

    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}
