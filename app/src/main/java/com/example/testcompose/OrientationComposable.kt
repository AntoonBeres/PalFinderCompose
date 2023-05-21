package com.example.testcompose

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import com.mutualmobile.composesensors.rememberAccelerometerSensorState
import com.mutualmobile.composesensors.rememberMagneticFieldSensorState


/*
Composable for getting the current orientation of the device relative to north
Uses the open-source compose-sensors package: https://github.com/mutualmobile/ComposeSensors
 */
@Composable
fun OrientationComposable(
    moved: (azimuth: Float) -> Unit = { _ -> }
): Double {
    //val orientationState = rememberRotationVectorSensorState()

    // Get accelerometer and magnetometer readings
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
    return azimuth.toDouble()

}
