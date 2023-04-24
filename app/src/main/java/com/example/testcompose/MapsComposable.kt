package com.example.testcompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState


// The actual maps composable
@Composable
fun MapsComposable(current_pos: LatLng, destination_marker: LatLng, waypoints: List<LatLng>) {
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