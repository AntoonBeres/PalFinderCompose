package com.example.testcompose

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode

/*
Converts a result from the "directions api" into a list of waypoints
 */
fun Array<out DirectionsRoute>?.toWaypoints(): List<com.google.android.gms.maps.model.LatLng> {
    return this?.last()!!.overviewPolyline.decodePath().map { point ->
        com.google.android.gms.maps.model.LatLng(point.lat, point.lng)
    }
}
/*
Smoothes the route by removing waypoints leading to segments shorter than a specified minimum segment length
Arguments:
min_segment_length: the minimum length of each segment
 */
fun List<com.google.android.gms.maps.model.LatLng>.smootheRoute(min_segment_length: Int): List<com.google.android.gms.maps.model.LatLng> {
    val w2 = ArrayList<com.google.android.gms.maps.model.LatLng>()
    w2.add(this[0])
    var lastPoint = this[0]
    for (i in 1 until this.size) {
        if (distanceLatLng(lastPoint.latitude, lastPoint.longitude, this[i].latitude, this[i].longitude) > min_segment_length) {
            w2.add(this[i])
            lastPoint = this[i]
        }
    }
    return w2.toList()
}
/*
Singleton object providing directions
 */
object DirectionsProvider {
    private val context: GeoApiContext = GeoApiContext.Builder()
        .apiKey(BuildConfig.GOOGLE_MAPS_API_KEY)
        .build()
    fun getRouteWaypoints(origin_lat: Double, origin_lng: Double, dest_lat: Double, dest_lng: Double): List<com.google.android.gms.maps.model.LatLng> {
        val origin = LatLng(origin_lat, origin_lng)
        val dest = LatLng(dest_lat, dest_lng)
        val directionResult = DirectionsApi
            .newRequest(context)
            .origin(origin)
            .destination(dest).mode(TravelMode.BICYCLING)
            .await()
        return directionResult.routes.toWaypoints().smootheRoute(30)
    }
}


