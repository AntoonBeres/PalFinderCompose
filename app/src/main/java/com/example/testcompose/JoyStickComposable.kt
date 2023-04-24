package com.example.testcompose

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.manalkaff.jetstick.JoyStick
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun JoyStickComposable() {
    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)

    val rotationMatrix = FloatArray(9)
    val orientationAngles = FloatArray(3)

    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)
    // North = 0degrees
    val azimuth = orientationAngles[0]

    val haptic = LocalHapticFeedback.current
    JoyStick(
        Modifier.absoluteOffset(100.dp, 550.dp),
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