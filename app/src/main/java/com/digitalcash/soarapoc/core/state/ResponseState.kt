package com.digitalcash.soarapoc.core.state

sealed interface ResponseState<out T> {
    data class Success<out T>(val data: T) : ResponseState<T>
    data object Loading : ResponseState<Nothing>
    class Error<out T>(val message: String) : ResponseState<T>
}