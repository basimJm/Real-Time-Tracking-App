package com.digitalcash.soarapoc.presentation.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.digitalcash.googlemap.core.helper.Permission
import com.digitalcash.soarapoc.R
import com.digitalcash.soarapoc.core.ui_component.AppButton
import com.digitalcash.soarapoc.core.ui_component.CustomBaseDialog
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract
import com.digitalcash.soarapoc.presentation.controller.vm.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val driverMarker = bitmapDescriptor(context, R.drawable.vector__4_)
    val customerMarker = bitmapDescriptor(context, R.drawable.home_color_icon)

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                state.customerLocation ?: LatLng(
                    0.0,
                    0.0
                ), 16f
            )
        }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Permission(permission = Manifest.permission.ACCESS_FINE_LOCATION) {
            LaunchedEffect(state.customerLocation == null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    state.customerLocation ?: LatLng(
                        0.0,
                        0.0
                    ), 16f
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    state.customerLocation?.let {
                        Marker(
                            title = "Origin",
                            state = MarkerState(position = it),
                            icon = customerMarker
                        )
                    }
                    state.driverLocationLocation?.let {
                        Marker(
                            title = "Destination",
                            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                            icon = driverMarker
                        )
                    }

                    if (state.routePoints.isNotEmpty()) {
                        state.routePoints.forEach {
                            Polyline(points = (it), color = Color.Red)
                        }
                    }
                }
                if (state.showRequestLoadingDialog) {
                    CustomBaseDialog(
                        title = "Order Request",
                        isLoadingDialog = true,
                        message = {
                            Text(
                                text = "Waiting Driver To Accept You Request",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 27.sp,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        },
                        onPositiveCallback = { viewModel.onEvent(MainContract.UiAction.OnCancelOrderClicked) },
                        positiveButton = stringResource(id = R.string.cancel_order),
                        positiveButtonColor = Color.Red
                    )
                }

                if (state.showRejectedDialog) {
                    CustomBaseDialog(
                        title = "Order Rejected",
                        isLoadingDialog = false,
                        message = {
                            Text(
                                text = "Your Order Has Been Rejected By Driver",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 27.sp,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        },
                        onPositiveCallback = { viewModel.onEvent(MainContract.UiAction.OnDismissRejectedDialogClicked) },
                        positiveButton = stringResource(id = R.string.ok)
                    )
                }

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }


                AppButton(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomCenter),
                    title = "Request Order",
                    enabled = !state.isLoading,
                    enableButtonColor = Color.Red,
                    isLoading = state.isLoading,
                    onClick = { viewModel.onEvent(MainContract.UiAction.OnRequestOrderClicked) })
            }
        }
    }
}


fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}