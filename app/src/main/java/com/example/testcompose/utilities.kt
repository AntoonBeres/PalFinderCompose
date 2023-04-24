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