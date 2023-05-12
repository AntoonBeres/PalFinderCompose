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
// Radians to degrees, simple utilities function
fun degrees(radians: Double): Double {
    return radians * (180 / Math.PI)
}
// Antoon Beres
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

