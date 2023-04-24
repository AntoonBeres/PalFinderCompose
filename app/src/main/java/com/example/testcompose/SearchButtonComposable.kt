package com.example.testcompose

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@Composable
fun SearchButtonComposable(
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
            Text("Select Destination")
        }
    }
}