package com.example.testcompose

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

// Search button
// This creates the button for selecting a destination by name using the google places api
// A lot of code taken from:
// https://stackoverflow.com/questions/70834787/implementing-google-places-autocomplete-textfield-implementation-in-jetpack-comp
@Composable
fun SearchButtonComposable(
    onDestinationSelected: (destination_selected: Place) -> Unit
) {
    val context = LocalContext.current

    // launch an intent for selecting a destination using the places API
    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                activityResult.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    onDestinationSelected(place) // Run "onDestinationSelected" with the selected place as argument
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation. do nothing
            }
        }
    }
    // Launch the overlay with an intent
    val launchMapInputOverlay = {
        Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY)
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        intentLauncher.launch(intent)
    }
    // Add visible search button
    Column {
        Button(onClick = launchMapInputOverlay, modifier = Modifier.width(150.dp)) {
            Text("Select Destination")
        }
    }
}