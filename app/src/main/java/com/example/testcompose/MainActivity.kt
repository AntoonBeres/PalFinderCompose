package com.example.testcompose

import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 This is like the main function, this is the entry point of our application
 Additionally it takes care of permissions. If location permissions aren't
 already granted, it requests location permissions at launch.

 The location tracker is initialized here so that recomposition of the
 PalFinderApp composable is triggered when location updates are received.
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // The permissions required for location data
            val multiplePermissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            // The surface on which our entire application is drawn
            Surface(Modifier.onPlaced { multiplePermissionState.launchMultiplePermissionRequest() }) {
                LocationPermission(multiplePermissionState = multiplePermissionState)
            }
        }
    }
}

// The global composable, initializes location data and sets everything up
@Composable
fun GlobalView(modifier: Modifier = Modifier) {
    var lastLocation: Location? by remember { mutableStateOf(null) }
    Surface(modifier) {
        LocationTracker(userMoved = {
            // Keep track of user-location and update "PalFinderApp" when a new location is received
            lastLocation = it
        })
        // the actual app
        PalFinderApp(lastLocation)
    }
}

// Tutorial used: https://betterprogramming.pub/jetpack-compose-request-permissions-in-two-ways-fd81c4a702c
// Handles location permissions and displays an empty screen with text if permissions aren't granted
// If permissions are granted, just displays the app
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(
    multiplePermissionState: MultiplePermissionsState,
) {
    PermissionsRequired(
        multiplePermissionsState = multiplePermissionState,
        permissionsNotGrantedContent = { Text(text = "Location permissions needed to run app", textAlign = TextAlign.Center) },
        permissionsNotAvailableContent = {  Text(text = "Location permissions needed to run app", textAlign = TextAlign.Center) }
    ) {
        // If permissions are granted initialize the app
        GlobalView()
    }
}












