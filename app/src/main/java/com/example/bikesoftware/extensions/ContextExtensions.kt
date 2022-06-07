package com.example.bikesoftware.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.bikesoftware.utils.BACKGROUND_LOCATION_PERMISSION
import com.example.bikesoftware.utils.FOREGROUND_LOCATION_PERMISSIONS

@RequiresApi(Build.VERSION_CODES.Q)
fun Context.areLocationPermissionsGranted(): Boolean {
    val permissions = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        true -> BACKGROUND_LOCATION_PERMISSION
        else -> FOREGROUND_LOCATION_PERMISSIONS
    }

    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}
