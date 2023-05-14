package com.example.testcompose

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.HandlerThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.mutualmobile.composesensors.SensorDelay
import com.mutualmobile.composesensors.SensorType
import com.mutualmobile.composesensors.rememberAccelerometerSensorState
import com.mutualmobile.composesensors.rememberGeomagneticRotationVectorSensorState
import com.mutualmobile.composesensors.rememberMagneticFieldSensorState
import com.mutualmobile.composesensors.rememberRotationVectorSensorState


/*
Uses the open-source compose-sensors package: https://github.com/mutualmobile/ComposeSensors
 */
@Composable
fun OrientationComposable(
    moved: (azimuth: Float) -> Unit = { _ -> }
) {
    //val orientationState = rememberRotationVectorSensorState()

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


    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)
    val azimuth = orientationAngles[0]

    moved(azimuth)

}
