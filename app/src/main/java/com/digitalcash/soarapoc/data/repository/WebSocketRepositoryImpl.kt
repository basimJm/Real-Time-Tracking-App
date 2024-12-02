package com.digitalcash.soarapoc.data.repository

import android.util.Log
import com.digitalcash.soarapoc.domain.entity.Event
import com.digitalcash.soarapoc.domain.repository.WebSocketRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WebSocketRepositoryImpl @Inject constructor(private val httpClient: OkHttpClient) :
    WebSocketRepository {
    private var webSocket: WebSocket? = null
    private val eventChannel = Channel<Event>()

    override suspend fun initSocket(userName: String): Boolean {
        val request =
            Request.Builder()
                .url("ws://10.0.2.2:8022/tracking-socket?username=$userName")
                .build()

        try {
            webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onMessage(webSocket: WebSocket, text: String) {
                    val event = Json.decodeFromString<Event>(text)
                    Log.d("webSocketLogger", "onMessage : $event")
                    eventChannel.trySend(event)

                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    Log.d("webSocketLogger", "OnOpen : $response")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.d("webSocketLogger", "onFailure : ${t}")
                }
            })

            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun sendEvent(event: Event) {
        try {
            webSocket?.send(event.toString()) ?: Log.d(
                "webSocketLogger",
                "Web Socket Is Not Connected"
            )
        } catch (e: Exception) {
            Log.d("webSocketLogger", "sendEvent : ${e.printStackTrace()}")
        }
    }

    override suspend fun receiveEvent(): Flow<Event> {
        return eventChannel.receiveAsFlow()
    }

    override suspend fun disconnect() {
        webSocket?.close(1000, "Client Disconnect")
        eventChannel.close()
    }

}