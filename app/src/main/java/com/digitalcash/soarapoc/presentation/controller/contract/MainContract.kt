package com.digitalcash.soarapoc.presentation.controller.contract

interface MainContract {
    data class UiState(
        val isLoading: Boolean = false,
        val userName: String = "",
        val isConnected: Boolean = false,
    )

    sealed interface UiAction {

    }

    sealed interface SideEffect {

    }
}