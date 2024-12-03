package com.digitalcash.soarapoc.presentation.controller.contract

import com.google.android.gms.maps.model.LatLng

interface MainContract {
    data class UiState(
        val isLoading: Boolean = false,
        val showDialog: Boolean = false,
        val customerLocation: LatLng? = null,
        val requestLoadingDialog: Boolean = false,
        val openMap: Boolean = false,
        val actionType: String = "",
        val userName: String = "",
        val isConnected: Boolean = false,
        val routePoints: List<List<LatLng>> = emptyList()
    )

    sealed interface UiAction {
        data object OnRequestOrderClicked : UiAction
    }

    sealed interface SideEffect {

    }
}