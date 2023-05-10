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


// Put everything together (search, maps, joystick, ..)
@Composable
fun PalFinderApp(current_loc: Location?, modifier: Modifier = Modifier) {
    var destination by remember { mutableStateOf(LatLng(1.35, 103.87)) }
    var waypoints: List<LatLng> by remember { mutableStateOf(emptyList()) }

    var navigate_running by remember { mutableStateOf(false) }

    val directions_receiver = Directions()

    Surface(modifier) {
        if (current_loc == null) {
            MapsComposable(destination, destination, waypoints)
        } else {
            val current_pos_latlng = LatLng(current_loc.latitude, current_loc.longitude)
            MapsComposable(current_pos_latlng, destination, waypoints)
        }
        //JoyStickComposable()
        Text(text = "navigate", modifier.absolutePadding(left = 180.dp, right = 5.dp, top = 0.dp, bottom = 0.dp))
        Switch(
            checked = navigate_running,
            onCheckedChange = {navigate_running = !navigate_running},
            modifier = Modifier
                .absolutePadding(left = 180.dp, right = 0.dp, top = 10.dp, bottom = 0.dp)
        )
        //Search button stuff, set destination marker + waypoints when destination is selected
        SearchButtonComposable { destination_selected ->
            run {
                destination_selected.latLng?.let { selectedLocation ->
                    destination = selectedLocation
                    if (current_loc != null) {
                        waypoints = directions_receiver.getRouteWaypoints(current_loc.latitude, current_loc.longitude, selectedLocation.latitude, selectedLocation.longitude)
                    }
                }
            }
        }
        // END of SearchButton stuff
        //NewJoystick()
        if(navigate_running){
            ImprovedJoystickController(){ x: Float, y: Float ->
                val y = -y
                val angle: Double = Math.atan2(y.toDouble(), x.toDouble()) * (180/ Math.PI)
                val zero_top = angle-90
                Log.d("JoyStick", "$zero_top")
            }
        }
    }
}