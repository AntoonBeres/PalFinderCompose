package com.example.testcompose

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import com.google.maps.DirectionsApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.isPopupLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.example.testcompose.ui.theme.TestComposeTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.google.maps.model.TravelMode
import com.manalkaff.jetstick.JoyStick
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.tan


import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


/*
This is like the main function, this is the entry point of our application
 */
class MainActivity : ComponentActivity() {
    private var lastLocation: Location? = null
    private var isLocationAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            globalView()
        }
    }
}


@Composable
private fun globalView(modifier: Modifier = Modifier) {
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
        palFinderView(lastLocation)
    }

}


@Composable
fun myComposable() {

    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    //...
}

object ApiProvider {
    //Custom context containing the GOOGLE_MAPS_API_KEY as required by DirectionsAPI
    fun getGeoContext(): GeoApiContext {
        return GeoApiContext.Builder()
            .apiKey(BuildConfig.GOOGLE_MAPS_API_KEY)
            .build()
    }
}

@Composable
private fun palFinderView(current_loc: Location?, modifier: Modifier = Modifier) {
    var destination by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    var waypoints: List<LatLng> by remember { mutableStateOf(emptyList()) }

    Surface(modifier) {
        if (current_loc == null) {
            testMapsFun(destination, destination, waypoints)
        } else {
            val current_pos_latlng = LatLng(current_loc.latitude, current_loc.longitude)
            testMapsFun(current_pos_latlng, destination, waypoints)
        }
        JstickTest()

        SearchButton { destination_selected ->
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
                            ).mode(TravelMode.WALKING).await()
                    directionResult.routes.forEach { route ->
                        val points = route.overviewPolyline.decodePath()
                        waypoints = points.map { point -> LatLng(point.lat, point.lng) }.toList()

                    }
                }
            }
        }
    }
}

@Composable
fun testMapsFun(current_pos: LatLng, destination_marker: LatLng, waypoints: List<LatLng>) {
    //Example location + camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(current_pos, 10f)
    }

    // Add zooming + "my location" button
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true,
                compassEnabled = false,
                rotationGesturesEnabled = true
            )
        )
    }
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))
    }

    // Initiallize map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,

        ) {
        //Add markers
        Marker(
            state = MarkerState(position = destination_marker),
            title = "Destination",
            snippet = "Navigation Destination"
        )
        Polyline(
            points = waypoints,
            visible = true,
            width = 10f,
        )

    }
}

@Composable
fun SearchButton(
    onDestinationSelected: (destination_selected: Place) -> Unit
) {
    val context = LocalContext.current
    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                it.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    onDestinationSelected(place)
                }
            }

            Activity.RESULT_CANCELED -> {
                // The user canceled the operation. do nothing
            }
        }
    }
    val launchMapInputOverlay = {
        Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY)
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }
    Column {
        Button(onClick = launchMapInputOverlay) {
            Text("Select Location")
        }
    }
}

@Composable
fun JstickTest() {
    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)

    val rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)

    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)
    // North = 0degrees
    val azimuth = orientationAngles[0]

    val haptic = LocalHapticFeedback.current
    JoyStick(
        Modifier.absoluteOffset(100.dp, 550.dp),
        size = 200.dp,
        dotSize = 50.dp
    ) { x: Float, y: Float ->

        //val angle = ((atan2(y,x)/ PI)*180f)- 90f
        val angle: Double = if (y < 0) -(((atan2(y, x) / PI) * 180f) - 90f) else (((atan2(y, x) / PI) * 180f) - 90f);

        if (angle < 20 && angle > -20){
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        Log.d("JoyStick", "$angle")

    }
}

fun radians(n: Double): Double {
    return n * (PI / 180);
}

fun degrees(n: Double): Double {
    return n * (180 / Math.PI);
}
fun bearing(startLat: Double, startLong: Double, endLat: Double, endLong: Double): Double {
    val startLat = radians(startLat);
    val startLong = radians(startLong);
    val endLat = radians(endLat);
    val endLong = radians(endLong);

    var dLong = endLong - startLong;

    val dPhi = ln(tan(endLat/2.0+Math.PI/4.0) / tan(startLat/2.0+Math.PI/4.0));

    if (abs(dLong) > PI){
        if (dLong > 0.0)
            dLong = -(2.0 * PI - dLong);
        else
            dLong = (2.0 * PI + dLong);
    }
    return (degrees(atan2(dLong, dPhi)) + 360.0) % 360.0;
}