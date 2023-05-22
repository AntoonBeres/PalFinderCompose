package com.example.testcompose

import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mutualmobile.composesensors.rememberAccelerometerSensorState
import com.mutualmobile.composesensors.rememberMagneticFieldSensorState
import kotlin.math.pow
import kotlin.math.sqrt


// The new "invisible" joystick, can be used by dragging from anywhere on the screen.
// Some of the code was inspired by the source code for the pre-made joystick component
// https://github.com/manalkaff/JetStick
@Composable
fun ImprovedJoystickController(
    moved: (x_drag: Float, y_drag: Float, deviceAzimuth: Double) -> Unit = { _, _, _ -> },

) {
    // The dragging directions
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


    // The location where dragging is started
    var startX by remember { mutableStateOf(0f) }
    var startY by remember { mutableStateOf(0f) }

    var azimuth by remember { mutableStateOf(0.0)}

    // Get magnetormeter and accelerometer readings
    val magneticFieldSensorState = rememberMagneticFieldSensorState()
    val accelerometerSensorState = rememberAccelerometerSensorState()
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)
    val rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)
    accelerometerReading[0] = accelerometerSensorState.xForce
    accelerometerReading[1] = accelerometerSensorState.yForce
    accelerometerReading[2] = accelerometerSensorState.zForce
    magnetometerReading[0] = magneticFieldSensorState.xStrength
    magnetometerReading[1] = magneticFieldSensorState.yStrength
    magnetometerReading[2] = magneticFieldSensorState.zStrength

    // Calculate device orientation from sensor readings
    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)

    // This is the angle we're interested in: the direction the device is pointed towards relative to the magnetic north
    azimuth = orientationAngles[0].toDouble()


    // Add a transparent red circle at the location the user starts to drag
    // This way the user knows where dragging started when looking at phone and gives
    // a visual queue as to where the user is dragging
    Canvas(modifier = Modifier.size(200.dp), onDraw = {
        drawCircle(color = Color.Red.copy(alpha = 0.3f), center= Offset(startX, startY+125))

    })




    // Box fills the entire screen except for a small portion at the top
    // Small portion at the top is so that the user can still switch navigation on/off
    // and select a destination, without input being captures by the virtual joystick
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(
                left = 0.dp,
                right = 0.dp,
                top = 50.dp, // some padding on the top for the search-button etc
                bottom = 0.dp
            )
            .fillMaxHeight().background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    offsetX = 0f
                    offsetY = 0f

                    startX = it.x
                    startY = it.y

                })
                { pointerInputChange: PointerInputChange, offset: Offset ->
                    pointerInputChange.consume()
                    offsetX += offset.x
                    offsetY += offset.y
                    // Convert screen positioning to coordinates by passing -offsetY instead of offsetY
                    // (the y-coordinate returned by dragging increases when going down and decreases when going up)
                    // Call the "moved" function (implemented by the caller) with the joystick orientation (as x,y coordinates)
                    // and device orientation
                    moved(
                        offsetX, -offsetY, azimuth
                    )
                }
            }
    ) {
        // Add a smaller transparent blue circle that moves with user dragging gestures
        Canvas(modifier = Modifier.size(30.dp), onDraw = {
            val reference_radius = 40000f
            val current_radius = offsetX.pow(2) + offsetY.pow(2)

            var x_offset = offsetX
            var y_offset = offsetY

            // The open-source joystick, converted to polar coordinates etc to get direction and radius
            // and then did a lot of unnecessary calculations
            // This is a simpler calculation that achieves the same thing.. lock the maximum movement range of the inner
            // joystick circle to the size of the big red circle
            if(current_radius > reference_radius) {
                val ratio = sqrt(current_radius/reference_radius)
                x_offset /= ratio
                y_offset /= ratio
            }
                drawCircle(color = Color.Blue.copy(alpha = 0.3f), center= Offset(startX+x_offset, startY+y_offset))

        })
    }


}

