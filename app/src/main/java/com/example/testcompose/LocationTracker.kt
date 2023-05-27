package com.example.testcompose

import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Manages all location related tasks for the app.
 * Official android documentation used:
 * https://developer.android.com/training/location/retrieve-current
 * https://developer.android.com/training/location/request-updates
 */
@Composable
fun LocationTracker(
    userMoved: (x: Location?) -> Unit = { _ -> }
) {
    // Store last location
    var lastLocation: Location? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    // Location provide
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // A location request, uses high accuracy location if available
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(60)
            setMaxUpdateDelayMillis(50)
        }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(userLocationResult: LocationResult) {
            super.onLocationResult(userLocationResult)
            if (userLocationResult.lastLocation == null) return
            lastLocation = userLocationResult.lastLocation
            // Whenever callback is received call the "userMoved" function (defined by the caller)
            // with the location-update as a parameter
            userMoved(lastLocation)
        }

    }
    // Check if permissions are available (required for fusedLocationClient.requestLocationUpdates)
    if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    // Get updates
    fusedLocationClient.requestLocationUpdates(
        locationRequest.build(), locationCallback, Looper.getMainLooper()
    )
}