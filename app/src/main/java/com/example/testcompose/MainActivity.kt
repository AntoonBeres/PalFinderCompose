package com.example.testcompose

import android.Manifest
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import com.google.maps.DirectionsApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode


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

//TODO create separate composable for sensor data instead of having it together with the joystick
@Composable
fun myComposable() {

    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    //...
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
            setMaxUpdateDelayMillis(5.seconds.toLong(DurationUnit.MILLISECONDS))
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
        PalFinderView(lastLocation)
    }
}
// Put everything together (search, maps, joystick, ..)
@Composable
private fun PalFinderView(current_loc: Location?, modifier: Modifier = Modifier) {
    var destination by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    var waypoints: List<LatLng> by remember { mutableStateOf(emptyList()) }

    Surface(modifier) {
        if (current_loc == null) {
            MapsComposable(destination, destination, waypoints)
        } else {
            val current_pos_latlng = LatLng(current_loc.latitude, current_loc.longitude)
            MapsComposable(current_pos_latlng, destination, waypoints)
        }
        JoyStickComposable()

        //Search button stuff, set destination marker + waypoints when destination is selected
        SearchButtonComposable { destination_selected ->
            run {
                destination_selected.latLng?.let { selectedLocation ->
                    destination = selectedLocation
                    val userLocationLatLng =
                        com.google.maps.model.LatLng(
                            current_loc!!.latitude,
                            current_loc.longitude
                        )

                    val directionResult =
                        DirectionsApi.newRequest(ApiProvider.getGeoContext())
                            .origin(userLocationLatLng)
                            .destination(
                                com.google.maps.model.LatLng(
                                    destination.latitude,
                                    destination.longitude
                                )
                            ).mode(TravelMode.BICYCLING).await()
                    directionResult.routes.forEach { route ->
                        val points = route.overviewPolyline.decodePath()
                        waypoints = points.map { point -> LatLng(point.lat, point.lng) }.toList()

                    }
                }
            }
        }
        // END of SearchButton stuff
    }
}


