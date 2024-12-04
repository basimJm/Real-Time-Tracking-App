package com.digitalcash.soarapoc.data.repository

import com.digitalcash.soarapoc.core.state.ResponseState
import com.digitalcash.soarapoc.data.remote.data_source.LocationGetter
import com.digitalcash.soarapoc.data.remote.model.DeviceLocationDto
import com.digitalcash.soarapoc.domain.repository.LocationRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationRepoImpl @Inject constructor(private val locationGetter: LocationGetter) : LocationRepo {
    override suspend fun getCurrentLocation(): Flow<ResponseState<DeviceLocationDto>> {
        return callbackFlow {
            trySend(ResponseState.Loading)
            locationGetter.getDeviceLocation {
                val deviceLoc = it
                if (deviceLoc != null) {
                    trySend(ResponseState.Success(deviceLoc)).isSuccess
                } else {
                    trySend(
                        ResponseState.Error("Failed To Acquire Location!!"),
                    )
                }
            }
            awaitClose { }
        }
    }
}