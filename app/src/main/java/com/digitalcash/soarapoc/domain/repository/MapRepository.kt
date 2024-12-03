package com.digitalcash.soarapoc.domain.repository

import com.digitalcash.soarapoc.core.state.ResponseState
import com.digitalcash.soarapoc.domain.entity.Route
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun getDirections(
        origin: LatLng,
        endPoint: LatLng
    ): Flow<ResponseState<Route>>
}