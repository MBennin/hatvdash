package com.matthewbennin.hatvdash.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

object HaWebSocketManager {
    private const val TAG = "HaWebSocketManager"
    private var webSocket: WebSocket? = null
    private var messageIdCounter = 1
    private val callbacks = ConcurrentHashMap<Int, (JSONObject) -> Unit>()

    private var isAuthenticated = false

    private val pendingMessages = mutableListOf<String>()

    fun connect (baseUrl: String, token: String) {
        val url = "ws://${baseUrl.removePrefix("http://")}/api/websocket"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        webSocket = client.newWebSocket(request, object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received: $text")
                val msg = JSONObject(text)
                when (msg.getString("type")) {
                    "auth_required" -> {
                        Log.d(TAG, "Auth required, sending token")
                        val auth = JSONObject()
                            .put("type", "auth")
                            .put("access_token", token)
                        webSocket.send(auth.toString())
                    }
                    "auth_invalid" -> {
                        Log.d(TAG, "Authentication failed: ${msg.optString("message")}")
                    }
                    "auth_ok" -> {
                        Log.d(TAG, "Authenticated with Home Assistant")
                        pendingMessages.forEach { this@HaWebSocketManager.webSocket?.send(it) }
                        pendingMessages.clear()
                        isAuthenticated = true
                    }
                    "result" -> {
                        Log.d(TAG, "Received result for request ${msg.optInt("id")}")
                        val id = msg.optInt("id")
                        callbacks.remove(id)?.invoke(msg)
                    }
                    "event" -> {
                        Log.d(TAG, "Received event for request ${msg.optInt("id")}")

                        val event = msg.optJSONObject("event") ?: return

                        if (event.getString("event_type") == "state_changed") {
                            val newState = event.optJSONObject("data")?.optJSONObject("new_state") ?: return
                            val entityId = newState.optString("entity_id") ?: return

                            //EntityStateManager.updateEntityState(entityId, newState) TODO: Add this
                        }
                    }
                    else -> {
                        Log.d(TAG, "Unhandled message type: ${msg.optString("type")}")
                    }
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error", t)
            }
        })
    }

    fun close() {
        webSocket?.close(1000, "App closed")
        webSocket = null
        isAuthenticated = false
        pendingMessages.clear()
        callbacks.clear()
        messageIdCounter = 1
    }

    private fun sendMessage(json: JSONObject) {
        val message = json.toString()
        if (isAuthenticated) {
            webSocket?.send(message)
        } else {
            pendingMessages.add(message)
        }
    }

    fun requestLovelaceJson(callback: (JSONObject) -> Unit) {
        val id = messageIdCounter++
        callbacks[id] = { response ->
            val result = response.optJSONObject("result")
            if (result != null) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    callback(result)
                }
            } else {
                Log.e(TAG, "Invalid Lovelace config response")
            }
        }
        val request = JSONObject()
            .put("id", id)
            .put("type", "lovelace/config")
        sendMessage(request)
    }
}