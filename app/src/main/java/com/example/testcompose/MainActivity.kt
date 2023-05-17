package com.example.testcompose

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

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

// The global composable, initializes location data and sets everything up
@Composable
private fun GlobalView(modifier: Modifier = Modifier) {
    var lastLocation: Location? by remember { mutableStateOf(null) }
    Surface(modifier) {
        LocationTracker(userMoved = {
            lastLocation = it
        })
        PalFinderApp(lastLocation)
    }
}










