package com.digitalcash.soarapoc.core.mvi_core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MVI<UiState, UiEvent, SideEffect> {
    val uiState: StateFlow<UiState>
    val sideEffect: Flow<SideEffect>

    fun onEvent(event: UiEvent)

    fun updateState(state: UiState.() -> UiState)

    fun updateState(state: UiState)

    fun CoroutineScope.emitSideEffect(effect: SideEffect)
}