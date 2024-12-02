package com.digitalcash.googlemap.core.state

sealed class ResponseState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ResponseState<T>(data)
    class Loading<T>() : ResponseState<T>()
    class Error<T>(message: String, data: T? = null) : ResponseState<T>(data, message)
}