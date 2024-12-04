package com.digitalcash.soarapoc.domain.entity

import com.google.android.gms.maps.model.LatLng

data class Route(
    val routePoints: List<List<LatLng>>
)
