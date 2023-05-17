package com.example.testcompose

import android.location.Location
import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2


// Put everything together (search-button, maps, joystick, ..)
@Composable
fun PalFinderApp(current_loc: Location?, modifier: Modifier = Modifier) {
    var destination by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    var waypoints: List<LatLng> by remember { mutableStateOf(emptyList()) }
    var navigationRunning by remember { mutableStateOf(false) }

    var user_loc by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    // The surface on which all components are drawn
    Surface(modifier) {
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
                destination_selected.latLng?.let { selectedLocation ->
                    destination = selectedLocation
                    if (current_loc != null) {
                        waypoints = DirectionsProvider.getRouteWaypoints(
                            current_loc.latitude,
                            current_loc.longitude,
                            selectedLocation.latitude,
                            selectedLocation.longitude
                        )
                    }
                }
            }
        }
        /*
         If tactile navigation is enabled, capture user-input via the virtual joystick and
         disable map-controls
         */
        if(navigationRunning){
            ImprovedJoystickController { x: Float, y: Float ->
                //Convert screen positioning to coordinates
                // (the y-coordinate returned by dragging increases when going down and decreases when going up)
                val angle: Double = atan2(y.toDouble(), x.toDouble()) * (180/ Math.PI)
                val zeroTop = if(angle > 90){
                    angle -90
                } else {
                    angle +270
                }
                //Log.d("JoyStick", "$zeroTop")
            }
        }
        OrientationComposable {
            //Log.d("azimuth", "$it")
        }


    }
}