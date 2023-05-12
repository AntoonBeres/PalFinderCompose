package com.example.testcompose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlin.math.atan


// The new "invisible" joystick, can be used by dragging from anywhere on the screen.
// Some of the code was inspired by the source code for the original joystick component
// https://github.com/manalkaff/JetStick
@Composable
fun ImprovedJoystickController(
    moved: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    //var theta by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Box fills the entire screen except for a small portion at the top
    // Small portion at the top is so that the user can still switch navigation on/off
    // and select a destination, without input being captures by the virtual joystick
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 0.dp, right = 0.dp, top = 50.dp, bottom = 0.dp) // some padding on the top for the search-button etc
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    offsetX = 0f
                    offsetY = 0f
                })
                { pointerInputChange: PointerInputChange, offset: Offset ->
                    val x = offsetX + offset.x
                    val y = offsetY + offset.y
                    pointerInputChange.consume()

                    //
                    /*
                    theta = if (x >= 0 && y >= 0) {
                        atan(y / x)
                    } else if (x < 0 && y >= 0) {
                        (Math.PI).toFloat() + atan(y / x)
                    } else if (x < 0 && y < 0) {
                        -(Math.PI).toFloat() + atan(y / x)
                    } else {
                        atan(y / x)
                    }

                    theta = (theta + Math.PI/2).toFloat()*/
                    offsetX += offset.x
                    offsetY += offset.y
                    moved(
                        offsetX, -offsetY
                    )
                }
            }
            .onGloballyPositioned {
                moved(
                    offsetX, -offsetY
                )
            }
    )

}