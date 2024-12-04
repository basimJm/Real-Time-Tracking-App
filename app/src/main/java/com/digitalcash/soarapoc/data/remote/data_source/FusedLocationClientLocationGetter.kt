package com.digitalcash.soarapoc.data.remote.data_source

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.digitalcash.soarapoc.data.remote.model.DeviceLocationDto
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class FusedLocationClientLocationGetter @Inject constructor(private val context: Context) :
    LocationGetter {

    override suspend fun getDeviceLocation(
        onResultCallback: (deviceLocationDto: DeviceLocationDto?) -> Unit,
    ) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            onResultCallback(null)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onResultCallback(
                    DeviceLocationDto(
                        location.latitude, location.longitude,
                    ),
                )
            } else onResultCallback(null)
        }
    }
}