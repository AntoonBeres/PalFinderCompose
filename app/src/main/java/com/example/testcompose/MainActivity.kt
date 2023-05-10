package com.example.testcompose

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.GeoApiContext


import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/*
This is like the main function, this is the entry point of our application
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlobalView()
        }
    }
}

// Copied from Kristi's version, I have honestly no idea why it doesn't work without this
object ApiProvider {
    //Custom context containing the GOOGLE_MAPS_API_KEY as required by DirectionsAPI
    fun getGeoContext(): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey(BuildConfig.GOOGLE_MAPS_API_KEY)
            .build()
    }
}

// The global composable, initializes location data and sets everything up
@Composable
private fun GlobalView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var lastLocation: Location? by remember { mutableStateOf(null) }
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(1.seconds.toLong(DurationUnit.MILLISECONDS))
            setMaxUpdateDelayMillis(3.seconds.toLong(DurationUnit.MILLISECONDS))
        }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(userLocationResult: LocationResult) {
            super.onLocationResult(userLocationResult)
            if (userLocationResult.lastLocation == null) return
            lastLocation = userLocationResult.lastLocation
        }
    }
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    fusedLocationClient.requestLocationUpdates(
        locationRequest.build(), locationCallback, Looper.getMainLooper()
    )
    Surface(modifier) {
        PalFinderApp(lastLocation)
    }
}










