package com.digitalcash.soarapoc.domain.repository

import com.digitalcash.soarapoc.domain.entity.Event
import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {
    suspend fun initSocket(userName: String): Boolean

    suspend fun sendEvent(event: Event)

    suspend fun receiveEvent(): Flow<Event>

    suspend fun disconnect()
}