package com.digitalcash.soarapoc.presentation.controller.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalcash.soarapoc.core.mvi_core.MVI
import com.digitalcash.soarapoc.core.mvi_core.mvi
import com.digitalcash.soarapoc.domain.repository.WebSocketRepository
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.SideEffect
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.UiAction
import com.digitalcash.soarapoc.presentation.controller.contract.MainContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val webSocketRepository: WebSocketRepository) :
    ViewModel(),
    MVI<UiState, UiAction, SideEffect> by mvi(UiState()) {
    init {
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

    fun receiveEvent() {
        viewModelScope.launch {
            webSocketRepository.receiveEvent().collectLatest { event ->
                updateState {
                    copy(
                        userName = event.userName
                    )
                }

            }
        }
    }
}