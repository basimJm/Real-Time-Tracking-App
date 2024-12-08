package com.digitalcash.soarapoc.presentation.controller.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalcash.soarapoc.core.mvi_core.MVI
import com.digitalcash.soarapoc.core.mvi_core.mvi
import com.digitalcash.soarapoc.core.state.ResponseState
import com.digitalcash.soarapoc.domain.entity.Event
import com.digitalcash.soarapoc.domain.enums.ActionType
import com.digitalcash.soarapoc.domain.repository.LocationRepo
import com.digitalcash.soarapoc.domain.repository.MapRepository
import com.digitalcash.soarapoc.domain.repository.WebSocketRepository
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.SideEffect
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.UiAction
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.UiState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val webSocketRepository: WebSocketRepository, private val mapRepository: MapRepository,
    private val locationRepo: LocationRepo
) :
    ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState()) {
    init {
        getCurrentLocation()
        viewModelScope.launch {
            val isConnected = webSocketRepository.initSocket("driver")
            updateState {
                copy(
                    isConnected = isConnected
                )
            }
        }
        receiveEvent()
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            locationRepo.getCurrentLocation().collectLatest { response ->
                when (response) {
                    is ResponseState.Error -> {
                        Log.d("webSocketLogger", "Location Error : ${response.message}")
                        updateState {
                            copy(isLoading = false)
                        }
                    }

                    ResponseState.Loading -> {
                        updateState {
                            copy(isLoading = true)
                        }
                    }

                    is ResponseState.Success -> {
                        Log.d("webSocketLogger", "Location Success : ${response.data}")
                        updateState {
                            copy(
                                isLoading = false,
                                driverLocation = LatLng(32.0728, 36.0870)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onEvent(event: UiAction) {
        viewModelScope.launch {
            when (event) {
                UiAction.OnAutoDriverSelectionClicked -> {
                    updateState { copy(receiveOrderDialog = false) }
                    val nearestDriver = getNearestDriver(uiState.value.drivers)
                    nearestDriver?.let {
                        webSocketRepository.sendEvent(
                            Event(
                                assignedTo = it.userName,
                                role = "merchant",
                                actionType = ActionType.ASSIGN_ORDER.action,
                                userName = "admin",
                                lat = uiState.value.orderInfo.lat,
                                long = uiState.value.orderInfo.long
                            )
                        )
                    }

                }

                UiAction.OnManualDriverSelectionClicked -> {
                    updateState { copy(receiveOrderDialog = false, isManualAssign = true) }
                }

                UiAction.OnDismissCanceledDialog -> updateState { copy(canceledOrderDialog = false) }

                is UiAction.OnDriverClicked -> {
                    val driversList = uiState.value.drivers
                    if (driversList.isNotEmpty()) {

                        webSocketRepository.sendEvent(
                            Event(
                                assignedTo = driversList[event.index].userName,
                                role = "merchant",
                                actionType = ActionType.ASSIGN_ORDER.action,
                                userName = "admin",
                                lat = uiState.value.orderInfo.lat,
                                long = uiState.value.orderInfo.long
                            )
                        )
                    }
                }

                UiAction.OnDismissRejectedDialogClicked -> updateState { copy(showRejectedDialog = false) }

            }
        }
    }

    private fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    private fun getNearestDriver(drivers: List<Event>): Event? {
        val order = uiState.value.orderInfo
        return drivers.minByOrNull { driver ->
            haversineDistance(order.lat, order.long, driver.lat, driver.long)
        }
    }


    private fun receiveEvent() {
        viewModelScope.launch {
            webSocketRepository.receiveEvent().collectLatest { event ->
                when (event.actionType) {
                    ActionType.REQUEST_ORDER.action -> {
                        updateState {
                            copy(
                                receiveOrderDialog = true,
                                orderInfo = event
                            )
                        }
                    }

                    ActionType.CONNECT.action -> {
                        if (event.role == "driver" && event.actionType == ActionType.CONNECT.action && !uiState.value.drivers.contains(
                                event
                            )
                        ) {
                            updateState {
                                copy(
                                    drivers = uiState.value.drivers.toMutableList().plus(event)
                                )
                            }
                            Log.d("webSocketLogger", "drivers list : ${uiState.value.drivers}")
                        }
                    }

                    ActionType.CANCEL_ORDER.action -> {
                        updateState { copy(receiveOrderDialog = false, canceledOrderDialog = true) }
                    }

                    ActionType.REJECT_ORDER.action -> {
                        updateState {
                            copy(
                                showRejectedDialog = true
                            )
                        }
                    }
                }
            }
        }
    }
}