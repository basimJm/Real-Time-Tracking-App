package com.digitalcash.soarapoc.presentation.controller.contract

import com.digitalcash.soarapoc.domain.entity.Event
import com.google.android.gms.maps.model.LatLng

interface MainContract {
    data class UiState(
        val isLoading: Boolean = false,
        val isManualAssign: Boolean = false,
        val showRejectedDialog: Boolean = false,
        val driverLocation: LatLng? = null,
        val receiveOrderDialog: Boolean = false,
        val canceledOrderDialog: Boolean = false,
        val userName: String = "",
        val isConnected: Boolean = false,
        val drivers: List<Event> = emptyList(),
        val orderInfo: Event = Event(
            role = "",
            actionType = "",
            userName = "",
            assignedTo = "",
            lat = 0.0,
            long = 0.0
        ),
        val routePoints: List<List<LatLng>> = emptyList()
    )

    sealed interface UiAction {
        data object OnAutoDriverSelectionClicked : UiAction
        data object OnManualDriverSelectionClicked : UiAction
        data object OnDismissCanceledDialog : UiAction
        data class OnDriverClicked(val index: Int) : UiAction
        data object OnDismissRejectedDialogClicked : UiAction
    }

    sealed interface SideEffect {

    }
}