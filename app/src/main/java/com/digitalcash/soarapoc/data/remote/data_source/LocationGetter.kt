package com.digitalcash.soarapoc.data.remote.data_source

import android.content.Context
import com.digitalcash.soarapoc.data.remote.model.DeviceLocationDto

interface LocationGetter {
    suspend fun getDeviceLocation(
         onResultCallback: (deviceLocationDto: DeviceLocationDto?) -> Unit,
    )
}
