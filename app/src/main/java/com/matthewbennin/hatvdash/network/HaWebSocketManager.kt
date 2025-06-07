package com.matthewbennin.hatvdash.network

import android.util.Log
import android.net.Uri
import com.matthewbennin.hatvdash.data.EntityStateManager
import okhttp3.*
import org.json.JSONArray
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
        // Support both http and https Home Assistant URLs
        val uri = Uri.parse(baseUrl)
        val scheme = if (uri.scheme == "https") "wss" else "ws"
        val host = uri.host ?: baseUrl
            .removePrefix("http://")
            .removePrefix("https://")
        val portPart = if (uri.port != -1) ":${uri.port}" else ""
        val url = "$scheme://$host$portPart/api/websocket"
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

                        //subscribeToStateChanges() // ✅ start watching for state updates
                    }
                    "result" -> {
                        Log.d(TAG, "Received result for request ${msg.optInt("id")}")
                        val id = msg.optInt("id")
                        callbacks.remove(id)?.invoke(msg)
                    }
                    "event" -> {

                        val event = msg.optJSONObject("event") ?: return

                        if (event.getString("event_type") == "state_changed") {
                            val newState = event.optJSONObject("data")?.optJSONObject("new_state") ?: return
                            val entityId = newState.optString("entity_id") ?: return

                            EntityStateManager.updateEntityState(entityId, newState)



//                            // Update only if forecast is now present
//                            if (entityId.startsWith("weather.") &&
//                                newState.optJSONObject("attributes")?.has("forecast") == true) {
//
//                                EntityStateManager.updateEntityState(entityId, newState)
//
//                                val forecast = newState
//                                    .optJSONObject("attributes")
//                                    ?.optJSONArray("forecast")
//
//                                Log.d("ForecastWatcher", "Received ${forecast?.length() ?: 0} forecast items for $entityId")
                            //}
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

    fun requestAllStates(callback: (JSONArray) -> Unit) {
        val id = messageIdCounter++
        callbacks[id] = { response ->
            val result = response.optJSONArray("result")
            if (result != null) {
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    callback(result)
                }
            } else {
                Log.e(TAG, "get_states result missing or malformed: $response")
            }
        }

        val request = JSONObject()
            .put("id", id)
            .put("type", "get_states")
        sendMessage(request)
    }

    fun requestWeatherForecastDebug(entityId: String) {
        val id = messageIdCounter++
        callbacks[id] = { response ->
            Log.d("WeatherForecastDebug", "Full forecast response:\n${response.toString(2)}")
        }

        val request = JSONObject()
            .put("id", id)
            .put("type", "weather/get_forecast")
            .put("entity_id", entityId)
            .put("forecast_type", "daily")

        sendMessage(request)
    }

    fun subscribeToStateChanges() {
        val id = messageIdCounter++
        val request = JSONObject()
            .put("id", id)
            .put("type", "subscribe_events")
            .put("event_type", "state_changed")
        sendMessage(request)
        Log.d(TAG, "Subscribed to state_changed events (id=$id)")
    }

    fun getForecast(entityId: String, type: String = "daily", callback: (JSONArray) -> Unit) {
        val id = messageIdCounter++

        callbacks[id] = { response ->
            val result = response.optJSONObject("result")
            val forecasts = result
                ?.optJSONObject("response")
                ?.optJSONObject(entityId)
                ?.optJSONArray("forecast")

            if (forecasts != null) {
                callback(forecasts)
            } else {
                Log.e(TAG, "No forecast returned for $entityId")
            }
        }

        val request = JSONObject()
            .put("id", id)
            .put("type", "call_service")
            .put("domain", "weather")
            .put("service", "get_forecasts")
            .put("target", JSONObject().put("entity_id", entityId))
            .put("service_data", JSONObject().put("type", type))
            .put("return_response", true)

        sendMessage(request)
        Log.d(TAG, "Requested $type forecast for $entityId (id=$id)")
    }

    fun callService(
        domain: String,
        service: String,
        entityId: String,
        data: JSONObject? = null
    ) {
        val id = messageIdCounter++
        val message = JSONObject().apply {
            put("id", id)
            put("type", "call_service")
            put("domain", domain)
            put("service", service)
            put("target", JSONObject().put("entity_id", entityId))
            if (data != null) {
                put("service_data", data)
            }
        }
        sendMessage(message)
    }





}