package com.digitalcash.soarapoc.domain.repository

import com.digitalcash.soarapoc.core.state.ResponseState
import com.digitalcash.soarapoc.data.remote.model.DeviceLocationDto
import kotlinx.coroutines.flow.Flow

/**
 * Created by Amer on 8/28/2022.
 */
interface LocationRepo {
    suspend fun getCurrentLocation(): Flow<ResponseState<DeviceLocationDto>>
}