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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.digitalcash.googlemap.core.helper.Permission
import com.digitalcash.soarapoc.R
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
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val driverMarkerIcon = bitmapDescriptor(context, R.drawable.car_svgrepo_com)
    val customerMarker = bitmapDescriptor(context, R.drawable.home_color_icon)

    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                state.driverLocation ?: LatLng(
                    0.0,
                    0.0
                ), 16f
            )
        }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Permission(permission = Manifest.permission.ACCESS_FINE_LOCATION) {
            LaunchedEffect(state.driverLocation == null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    state.driverLocation ?: LatLng(
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
                    state.drivers.forEachIndexed { index, driver ->
                        Marker(
                            state = MarkerState(position = LatLng(driver.lat, driver.long)),
                            title = driver.userName,
                            icon = driverMarkerIcon,
                            onClick = {
                                viewModel.onEvent(MainContract.UiAction.OnDriverClicked(index))
                                true
                            }
                        )
                    }
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

                if (state.receiveOrderDialog) {
                    CustomBaseDialog(
                        title = "New Order",
                        isLoadingDialog = false,
                        message = {
                            Text(
                                text = "You Received New Order",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 27.sp,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        },
                        onPositiveCallback = { viewModel.onEvent(MainContract.UiAction.OnAutoDriverSelectionClicked) },
                        positiveButton = "Auto",
                        negativeButton = R.string.manual,
                        onNegativeCallback = { viewModel.onEvent(MainContract.UiAction.OnManualDriverSelectionClicked) }
                    )
                }

                if (state.canceledOrderDialog) {
                    CustomBaseDialog(
                        title = "Order Canceled!!",
                        isLoadingDialog = false,
                        message = {
                            Text(
                                text = "Order Canceled By The Customer",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 27.sp,
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        },
                        negativeButton = R.string.ok,
                        onNegativeCallback = { viewModel.onEvent(MainContract.UiAction.OnDismissCanceledDialog) }
                    )
                }

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }


//                AppButton(
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .align(Alignment.BottomCenter),
//                    title = "Request Order",
//                    enabled = !state.isLoading,
//                    enableButtonColor = Color.Blue,
//                    isLoading = state.isLoading,
//                    onClick = { viewModel.onEvent(MainContract.UiAction.OnAcceptOrderClicked) })
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