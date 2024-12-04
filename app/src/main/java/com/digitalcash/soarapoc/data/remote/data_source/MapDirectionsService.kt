package com.digitalcash.soarapoc.data.remote.data_source

import com.digitalcash.soarapoc.BuildConfig
import com.digitalcash.soarapoc.data.remote.model.DirectionsDto
import retrofit2.http.GET

interface MapDirectionsService {

    @GET("directions/json")
    suspend fun getDirections(
        @retrofit2.http.Query("origin") originLatLng: String,
        @retrofit2.http.Query("destination") endPointLatLang: String,
        @retrofit2.http.Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): retrofit2.Response<DirectionsDto>

}