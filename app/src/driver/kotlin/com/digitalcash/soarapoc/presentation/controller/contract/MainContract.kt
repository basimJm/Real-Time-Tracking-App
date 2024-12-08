package com.digitalcash.soarapoc.presentation.controller.contract

import com.google.android.gms.maps.model.LatLng

interface MainContract {
    data class UiState(
        val isLoading: Boolean = false,
        val isDriverAvailable: Boolean = true,
        val showDialog: Boolean = false,
        val driverLocation: LatLng? = null,
        val customerLocation: LatLng? = null,
        val receiveOrderDialog: Boolean = false,
        val canceledOrderDialog: Boolean = false,
        val userName: String = "",
        val isConnected: Boolean = false,
        val routePoints: List<List<LatLng>> = emptyList()
    )

    sealed interface UiAction {
        data object OnAcceptOrderClicked : UiAction
        data object OnRejectOrderClicked : UiAction
        data object OnDismissCanceledDialog : UiAction
    }

    sealed interface SideEffect {

    }
}