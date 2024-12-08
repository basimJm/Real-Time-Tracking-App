package com.digitalcash.soarapoc.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val role: String,
    val actionType: String,
    val userName: String,
    val assignedTo:String,
    val lat: Double,
    val long: Double,
)
