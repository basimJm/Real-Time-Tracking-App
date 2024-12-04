package com.digitalcash.soarapoc.domain.entity

import com.google.android.gms.maps.model.LatLng

data class MapCoordination(
    val lat: Double,
    val lng: Double,
    val id: Int? = null
)
