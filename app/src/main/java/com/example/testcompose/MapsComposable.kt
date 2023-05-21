package com.example.testcompose

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState


// The actual maps composable:
// Draws an interactive map on the screen
// Based on official documentation on how to use the component
// : https://developers.google.com/maps/documentation/android-sdk/maps-compose
@Composable
fun MapsComposable(current_pos: LatLng, destination_marker: LatLng, waypoints: List<LatLng>, navEnabled: Boolean) {
    //Example location + camera position


    val center_pos by remember {
        mutableStateOf(current_pos)
    }


    // If navigation is enabled, make the camera zoom in and track the user location
    // otherwise, just give the user manual control over the camera
    val cameraPositionState = if(navEnabled) {
        CameraPositionState(CameraPosition.fromLatLngZoom(center_pos, 20f))
    } else {
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(center_pos, 14f)
        }
    }

    //var cameraPositionState = CameraPosition.fromLatLngZoom(current_pos, 10f)


    // Add zooming buttons + "my location" button and enable gestures
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true,
                compassEnabled = false,
                rotationGesturesEnabled = true,
                zoomControlsEnabled = true
            )
        )
    }
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))
    }
    // Initiallize the map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        ) {
        //Add markers, a marker is added at the destination
        Marker(
            state = MarkerState(position = destination_marker),
            title = "Destination",
            snippet = "Navigation Destination"
        )
        // Plot route of intermediate waypoints and draw lines between them
        Polyline(
            points = waypoints,
            visible = true,
            width = 10f,
        )
    }
}