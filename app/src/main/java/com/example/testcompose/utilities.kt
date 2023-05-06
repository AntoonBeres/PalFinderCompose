package com.example.testcompose

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ln
import kotlin.math.tan

fun radians(n: Double): Double {
    return n * (PI / 180);
}
fun degrees(n: Double): Double {
    return n * (180 / Math.PI);
}
fun getBearing(startLat: Double, startLong: Double, endLat: Double, endLong: Double): Double {
    val startLat = radians(startLat);
    val startLong = radians(startLong);
    val endLat = radians(endLat);
    val endLong = radians(endLong);

    var dLong = endLong - startLong;

    val dPhi = ln(tan(endLat/2.0+Math.PI/4.0) / tan(startLat/2.0+Math.PI/4.0));

    if (abs(dLong) > PI){
        if (dLong > 0.0)
            dLong = -(2.0 * PI - dLong);
        else
            dLong = (2.0 * PI + dLong);
    }
    return (degrees(atan2(dLong, dPhi)) + 360.0) % 360.0;
}


fun distanceLatLng(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double{  // generally used geo measurement function
    val R = 6378.137; // Radius of earth in KM
    val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
    val dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
    val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    val d = R * c;
    return d * 1000; // meters
}