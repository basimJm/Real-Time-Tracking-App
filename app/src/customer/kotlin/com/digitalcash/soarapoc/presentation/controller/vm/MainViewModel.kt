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
            val isConnected = webSocketRepository.initSocket("basim")
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
                        updateState {
                            copy(
                                isLoading = false,
                                customerLocation = LatLng(response.data.lat, response.data.lng)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onEvent(event: UiAction) {
        when (event) {
            UiAction.OnRequestOrderClicked -> requestOrder()

            UiAction.OnCancelOrderClicked -> cancelOrder()

            UiAction.OnDismissRejectedDialogClicked -> updateState { copy(showRejectedDialog = false) }
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

    private fun requestOrder() {
        viewModelScope.launch {
            uiState.value.customerLocation?.let {
                val eventBody = Event(
                    role = "customer",
                    actionType = ActionType.REQUEST_ORDER.action,
                    userName = "basim",
                    lat = it.latitude,
                    long = it.longitude
                )
                webSocketRepository.sendEvent(eventBody)
                updateState {
                    copy(
                        showRequestLoadingDialog = true,
                    )
                }
            }
        }
    }

    private fun cancelOrder() {
        viewModelScope.launch {
            uiState.value.customerLocation?.let {
                val eventBody = Event(
                    role = "customer",
                    actionType = ActionType.CANCEL_ORDER.action,
                    userName = "basim",
                    lat = it.latitude,
                    long = it.longitude
                )
                webSocketRepository.sendEvent(eventBody)
                updateState {
                    copy(
                        showRequestLoadingDialog = false,
                    )
                }
            }
        }
    }

    private fun receiveEvent() {
        viewModelScope.launch {
            webSocketRepository.receiveEvent().collectLatest { event ->
                when (event.actionType) {
                    ActionType.ACCEPT_ORDER.action, ActionType.NAVIGATION.action -> {
                        updateState {
                            copy(
                                showRequestLoadingDialog = false,
                                driverLocationLocation = LatLng(event.lat, event.long)
                            )
                        }

                        uiState.value.customerLocation?.let {
                            getDirections(LatLng(event.lat, event.long), it)
                        }
                    }

                    ActionType.REJECT_ORDER.action -> {
                        updateState {
                            copy(
                                showRequestLoadingDialog = false,
                                showRejectedDialog = true
                            )
                        }
                    }

                    ActionType.NAVIGATION.action -> {
                        updateState { copy(driverLocationLocation = LatLng(event.lat, event.long)) }
                    }
                }
            }
        }
    }
}