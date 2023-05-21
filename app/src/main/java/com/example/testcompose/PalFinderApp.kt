package com.example.testcompose

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs



// Put everything together (search-button, maps, joystick, ..)
@Composable
fun PalFinderApp(current_loc: Location?, modifier: Modifier = Modifier) {
    // The selected destination
    var destination by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    // List of waypoints
    var waypoints: List<LatLng> by remember { mutableStateOf(emptyList()) }
    // Whether tactile navigation is enabled or not
    var navigationRunning by remember { mutableStateOf(false) }


    // User location
    var user_loc by remember { mutableStateOf(LatLng(1.35, 103.87)) }

    // The bearing (degrees relative to north) between current location and next waypoint
    //var bearing: Double? = null
    var nextWaypointIndex: Int by remember { mutableStateOf(0)}

    // The local context
    val ctx = LocalContext.current

    // Initialize haptic feedback for devices that don't support custom vibration patterns
    val haptic = LocalHapticFeedback.current

    // Initialize haptic feedback for devices that support custom vibration patterns
    val vibrator: Vibrator = remember {
            if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager =
                    ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
    }

    // Create custom vibration effect
    val customVibrationLong = remember {VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)}


    // The surface on which all components are drawn
    Surface(modifier) {
        // If the current location is not (yet) available, initialize map to a default location
        if (current_loc == null) {
            MapsComposable(user_loc, destination, waypoints, navigationRunning)
        } else {
            user_loc = LatLng(current_loc.latitude, current_loc.longitude)
            MapsComposable(user_loc, destination, waypoints, navigationRunning)
        }

        // A switch to turn tactile navigation on or off
        Text(text = "navigate", modifier.absolutePadding(left = 180.dp, right = 5.dp, top = 0.dp, bottom = 0.dp))
        Switch(
            checked = navigationRunning,
            onCheckedChange = {navigationRunning = !navigationRunning},
            modifier = Modifier
                .absolutePadding(left = 180.dp, right = 0.dp, top = 10.dp, bottom = 0.dp)
        )

        // Search button for selecting a destination to navigate to
        SearchButtonComposable { destination_selected ->
            run {
                // Upon selecting a destination initialize waypoints
                destination_selected.latLng?.let { selectedLocation ->
                    destination = selectedLocation
                    if (current_loc != null) {
                        waypoints = DirectionsProvider.getRouteWaypoints(
                            current_loc.latitude,
                            current_loc.longitude,
                            selectedLocation.latitude,
                            selectedLocation.longitude
                        )
                        nextWaypointIndex = 0
                    }
                }
            }
        }


        if(!waypoints.isEmpty()){
            // If the distance to a waypoint is smaller than 15meters ->
            // consider the user to have passed the waypoint
            if(distanceLatLng(user_loc.latitude, user_loc.longitude, waypoints[nextWaypointIndex].latitude, waypoints[nextWaypointIndex].longitude) < 8) {
                nextWaypointIndex += 1
            }
            // If the distance to the nearest waypoint is greater than 150 meters ->
            // the user has strayed too far -> reinitialize waypoints
            if(distanceLatLng(user_loc.latitude, user_loc.longitude, waypoints[0].latitude, waypoints[0].longitude) > 150) {
                waypoints = DirectionsProvider.getRouteWaypoints(
                    user_loc.latitude,
                    user_loc.longitude,
                    destination.latitude,
                    destination.longitude
                )
                nextWaypointIndex = 0
            }

        }

        /*
         If tactile navigation is enabled, capture user-input via the virtual joystick and
         disable map-controls
         */
        if(navigationRunning){
            // Tactile navigation
            ImprovedJoystickController { x: Float, y: Float, azimuth ->
                if(!waypoints.isEmpty()){
                    // Calculate bearing between current location and next waypoint
                    val bearing = getBearing(user_loc.latitude, user_loc.longitude, waypoints[nextWaypointIndex].latitude, waypoints[nextWaypointIndex].longitude)

                    // Get user-joystick orientation as an angle instead of coordinates
                    val zeroTop = joystickPosToAngle(x,y)

                    // difference between device orientation and bearing of next waypoint
                    val orientation_diff =  degrees(azimuth) - bearing

                    // Get difference difference between difference in bearing/orientation and joystick orientation
                    val relative = abs(zeroTop-orientation_diff)

                    // Deal with circles, -180degrees = +180 degrees.. if difference is 360 degrees, difference is 0 degrees
                    val relativeCircle = abs(relative-360)


                    // Vibrate if finger pointed in correct direction
                    if(relative < 10 || relativeCircle < 10){

                        // If custom vibration is supported
                        vibrator.vibrate(customVibrationLong)


                    }
                }
            }
        }

    }
}


