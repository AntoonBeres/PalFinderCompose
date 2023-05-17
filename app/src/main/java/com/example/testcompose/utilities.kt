package com.example.testcompose

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/*
 This file contains all non-compose and non-ui related functions
 like calculating the bearing and distance between 2 coordinates given their latitude and longitude
 */

//Antoon Beres
// Degrees to radians, simple utility function
fun radians(degrees: Double): Double {
    return degrees * (PI / 180)
}
//Antoon Beres
// Radians to degrees, simple utility function
fun degrees(radians: Double): Double {
    return radians * (180 / Math.PI)
}
// Antoon Beres
// Function for getting the bearing (relative to north) from 2 coordinatess
// Code ported from following python code:
// https://geoffruddock.com/calculate-angle-between-coordinates-with-redshift-udfs/
fun getBearing(startLat: Double, startLong: Double, endLat: Double, endLong: Double): Double {
    val startLatRad = radians(startLat)
    val startLongRad = radians(startLong)
    val endLatRad = radians(endLat)
    val endLongRad = radians(endLong)

    var dLong = endLongRad - startLongRad

    val dPhi = ln(tan(endLatRad/2.0+Math.PI/4.0) / tan(startLatRad/2.0+Math.PI/4.0))

    if (abs(dLong) > PI){
        dLong = if (dLong > 0.0)
            -(2.0 * PI - dLong)
        else
            (2.0 * PI + dLong)
    }
    return (degrees(atan2(dLong, dPhi)) + 360.0) % 360.0
}

// Antoon Beres
// Function for getting the distance in meters between 2 coordinates
// Adapted from : https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
fun distanceLatLng(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double{  // generally used geo measurement function
    val r = 6378.137 // Radius of earth in KM
    val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180
    val dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180
    val a = sin(dLat/2) * sin(dLat/2) +
            cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
            sin(dLon/2) * sin(dLon/2)
    val c = 2 * atan2(sqrt(a), sqrt(1-a))
    val d = r * c
    return d * 1000 // meters
}

fun joystickPosToAngle(x: Float, y: Float): Double {
    val angle: Double = atan2(y.toDouble(), x.toDouble()) * (180/ Math.PI)
    // make 0 the top position
    var zeroTop = if(angle > 90){
        angle -90
    } else {
        angle +270
    }
    // deal with values in [-180, 180], just like the orientation values
    if(zeroTop > 180) {
        zeroTop -= 360
    }
    return zeroTop
}

