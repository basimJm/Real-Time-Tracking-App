package com.digitalcash.soarapoc.data.repository

import com.digitalcash.soarapoc.core.state.ResponseState
import com.digitalcash.soarapoc.data.remote.data_source.MapDirectionsService
import com.digitalcash.soarapoc.domain.entity.Route
import com.digitalcash.soarapoc.domain.repository.MapRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapDirectionsService: MapDirectionsService
) : MapRepository {
    override suspend fun getDirections(
        origin: LatLng,
        endPoint: LatLng
    ): Flow<ResponseState<Route>> {
        return flow {
            emit(ResponseState.Loading)

            try {
                val response = mapDirectionsService.getDirections(
                    originLatLng = "${origin.latitude},${origin.longitude}",
                    endPointLatLang = "${endPoint.latitude},${endPoint.longitude}"
                )

                if (response.isSuccessful && response.body() != null) {

                    val polyLinePoints = try {
                        response.body()!!.routes[0].legs[0].steps.map { step ->
                            step.polyline.decodePolyline(step.polyline.points)
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    emit(ResponseState.Success(data = Route(routePoints = polyLinePoints)))
                } else {
                    emit(ResponseState.Error(response.message()))
                }
            } catch (e: Exception) {
                emit(ResponseState.Error(e.message.toString()))
            }
        }
    }
}