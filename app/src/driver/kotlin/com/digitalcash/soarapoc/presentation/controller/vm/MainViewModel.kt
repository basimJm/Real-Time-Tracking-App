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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            val isConnected = webSocketRepository.initSocket("myDriver")
            updateState {
                copy(
                    isConnected = isConnected, userName = "myDriver"
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
                        webSocketRepository.sendEvent(
                            Event(
                                role = "driver",
                                actionType = ActionType.CONNECT.action,
                                userName = "myDriver",
                                assignedTo = "",
//                                lat = 32.0728,
//                                long = 36.0870,
                                lat = 32.0728,
                                long = 36.0880
                            )
                        )
                    }
                }
            }
        }
    }



    override fun onEvent(event: UiAction) {
        when (event) {
            UiAction.OnAcceptOrderClicked -> {
                Log.d("webSocketLogger", "sendEvent : clicked")
                acceptOrder()
            }

            UiAction.OnRejectOrderClicked -> rejectOrder()
            UiAction.OnDismissCanceledDialog -> updateState { copy(canceledOrderDialog = false) }
        }
    }

    private fun rejectOrder() {
        viewModelScope.launch {
            uiState.value.driverLocation.let {
                Log.d("webSocketLogger", "sendEvent : clicked")
                val eventBody = Event(
                    role = "driver",
                    actionType = ActionType.REJECT_ORDER.action,
                    userName = "bazzsim",
                    assignedTo = "",
                    lat = 32.0728,
                    long = 36.0870
                )
                webSocketRepository.sendEvent(eventBody)
                updateState {
                    copy(
                        receiveOrderDialog = false,
                    )
                }
            }
        }
    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        viewModelScope.launch {
            mapRepository.getDirections(origin, destination).collectLatest { response ->
                when (response) {
                    is ResponseState.Error -> {
                        updateState {
                            copy(isLoading = false)
                        }
                    }

                    is ResponseState.Loading -> {
                        updateState {
                            copy(isLoading = true)
                        }
                    }

                    is ResponseState.Success -> {
                        Log.d("TrackingService", "new routes ${response.data.routePoints}")
                        updateState {
                            copy(
                                isLoading = false,
                                routePoints = response.data.routePoints
                            )
                        }
                    }
                }
            }
        }
    }

    private fun acceptOrder() {
        viewModelScope.launch {
            uiState.value.driverLocation.let {
                Log.d("webSocketLogger", "sendEvent : clicked")
                val eventBody = Event(
                    role = "driver",
                    actionType = ActionType.ACCEPT_ORDER.action,
                    userName = "bazzsim",
                    assignedTo = "",
                    lat = 32.0728,
                    long = 36.0870
                )
                webSocketRepository.sendEvent(eventBody)
                updateState {
                    copy(
                        receiveOrderDialog = false,
                        isDriverAvailable = false
                    )
                }
                uiState.value.driverLocation?.let {
                    getDirections(
                        it,
                        LatLng(
                            uiState.value.customerLocation?.latitude ?: 0.0,
                            uiState.value.customerLocation?.longitude ?: 0.0
                        )
                    )
                }

                delay(2000)
                startNavigation()
            }
        }
    }

    private fun startNavigation() {
        viewModelScope.launch {
            if (uiState.value.routePoints.isNotEmpty()) {
                uiState.value.routePoints.forEach { first ->
                    first.forEach {
                        val eventBody = Event(
                            role = "driver",
                            actionType = ActionType.NAVIGATION.action,
                            userName = "bazzsim",
                            assignedTo = "",
                            lat = it.latitude,
                            long = it.longitude
                        )
                        delay(1000)
                        webSocketRepository.sendEvent(eventBody)
                        updateState { copy(driverLocation = LatLng(it.latitude, it.longitude)) }
                    }
                }
            }
        }
    }

    private fun receiveEvent() {
        viewModelScope.launch {
            webSocketRepository.receiveEvent().collectLatest { event ->
                when (event.actionType) {
                    ActionType.ASSIGN_ORDER.action -> {
                        if (event.assignedTo == uiState.value.userName && uiState.value.isDriverAvailable)
                            updateState {
                                copy(
                                    receiveOrderDialog = true,
                                    customerLocation = LatLng(event.lat, event.long)
                                )
                            }
                    }

                    ActionType.CANCEL_ORDER.action -> {
                        updateState { copy(receiveOrderDialog = false, canceledOrderDialog = true) }
                    }
                }
            }
        }
    }
}