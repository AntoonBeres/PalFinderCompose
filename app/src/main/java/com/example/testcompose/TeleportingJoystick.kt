package com.example.testcompose

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.manalkaff.jetstick.JoyStick
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
private fun TeleportingJoystick() {
    var posX by remember { mutableStateOf(500f) }
    var posY by remember { mutableStateOf(1500f) }

    // Initialize haptic feedback
    val haptic = LocalHapticFeedback.current

    // Initialize sensor data
    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)

    val rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)

    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)
    // North = 0degrees
    val azimuth = orientationAngles[0]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(left = 0.dp, right = 0.dp, top = 50.dp, bottom = 0.dp)
            .fillMaxHeight()
            // .background(Color.Blue)
            .pointerInput(Unit) {
                detectTapGestures { taplocation ->
                    posX = taplocation.x
                    posY = taplocation.y
                }
            }
    ) {
        JoyStick(
            Modifier
                .offset { IntOffset(posX.roundToInt() - 250, posY.roundToInt() - 250) }
                .alpha(0.5f),
            size = 200.dp,
            dotSize = 50.dp
        ) { x: Float, y: Float ->

            //val angle = ((atan2(y,x)/ PI)*180f)- 90f
            val angle: Double = if (y < 0) -(((atan2(y, x) / PI) * 180f) - 90f) else (((atan2(y, x) / PI) * 180f) - 90f);

            if (angle < 20 && angle > -20){
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            Log.d("JoyStick", "$angle")
        }
    }
}

