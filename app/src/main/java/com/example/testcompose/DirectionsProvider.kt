package com.example.testcompose

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode

/**
 This file provides an interface to the google "Directions" API
 It converts a result from the "directions api" into a list of waypoints
 The waypoints are subsequently processed in order to provide a smooth route
 */
fun Array<out DirectionsRoute>?.toWaypoints(): List<com.google.android.gms.maps.model.LatLng> {
    return this?.last()!!.overviewPolyline.decodePath().map { point ->
        com.google.android.gms.maps.model.LatLng(point.lat, point.lng)
    }
}
/*
Smoothes the route by removing waypoints
leading to only having segments longer than a specified minimum segment length
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
Singleton object for getting directions from one point to another
 */
object DirectionsProvider {
    // Build the API-context
    private val context: GeoApiContext = GeoApiContext.Builder()
        .apiKey(BuildConfig.GOOGLE_MAPS_API_KEY)
        .build()
    // Get a route between 2 coordinates, given their latitude and longitude
    fun getRouteWaypoints(origin_lat: Double, origin_lng: Double, dest_lat: Double, dest_lng: Double): List<com.google.android.gms.maps.model.LatLng> {
        val origin = LatLng(origin_lat, origin_lng)
        val dest = LatLng(dest_lat, dest_lng)
        val directionResult = DirectionsApi
            .newRequest(context)
            .origin(origin)
            .destination(dest).mode(TravelMode.BICYCLING) //"BICYCLING" gives a smoother route than "WALKING"
            .await()
        // Convert to waypoints and smooth the route
        return directionResult.routes.toWaypoints().smootheRoute(10)
    }
}


