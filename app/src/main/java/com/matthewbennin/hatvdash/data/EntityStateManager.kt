package com.matthewbennin.hatvdash.data

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import org.json.JSONArray
import org.json.JSONObject

object EntityStateManager {
    private val trackedEntities = mutableSetOf<String>()
    private val entitiesRequiringIcons = mutableSetOf<String>()
    private val entityStates = mutableStateMapOf<String, JSONObject>()

    private val weatherForecasts = mutableStateMapOf<String, JSONArray>()

    /** Public read-only view for observing in Compose */
    val stateMap: Map<String, JSONObject> get() = entityStates

    fun trackEntity(entityId: String, needsIcon: Boolean = false) {
        trackedEntities.add(entityId)
        if (needsIcon) entitiesRequiringIcons.add(entityId)
    }

    fun clear() {
        trackedEntities.clear()
        entitiesRequiringIcons.clear()
        entityStates.clear()
    }

    fun loadFilteredStates(json: JSONArray) {
        for (i in 0 until json.length()) {
            val entity = json.getJSONObject(i)
            val id = entity.optString("entity_id")
            if (id in trackedEntities) {
                entityStates[id] = entity
                Log.d("EntityStateManager", "Stored state for $id")
            }
        }
        Log.d("EntityStateManager", "Final state map size: ${entityStates.size}")
    }

    fun updateEntityState(entityId: String, newState: JSONObject) {
        if (entityId in trackedEntities) {
            entityStates[entityId] = newState
            Log.d("EntityStateManager", "Updated state for $entityId: ${newState.optString("state")}")
        }
    }

    fun getIconForEntity(entityId: String?): String? {
        return if (entityId in entitiesRequiringIcons) {
            entityStates[entityId]?.optJSONObject("attributes")?.optString("icon")
        } else null
    }

    fun getFriendlyNameForEntity(entityId: String?): String? {
        return entityStates[entityId]?.optJSONObject("attributes")?.optString("friendly_name")
    }

    fun getTrackedEntities(): Set<String> = trackedEntities.toSet()

    fun pruneUntrackedStates() {
        val keysToRemove = entityStates.keys.filter { it !in trackedEntities }
        keysToRemove.forEach { entityStates.remove(it) }
        Log.d("EntityStateManager", "Pruned ${keysToRemove.size} untracked entities")

        Log.d("EntityStateManager", "${entityStates.size} tracked")

        Log.d("EntityStateManager", "$trackedEntities")
    }

    fun getInitialStates() {
        HaWebSocketManager.requestAllStates { jsonArray ->
            preloadAllStates(jsonArray)
        }
    }

    fun preloadAllStates(json: JSONArray) {
        for (i in 0 until json.length()) {
            val entity = json.getJSONObject(i)
            val id = entity.optString("entity_id")
            entityStates[id] = entity
        }
        Log.d("EntityStateManager", "Preloaded ALL entity states: ${entityStates.size}")
    }

    fun requestForecastForEntity(entityId: String, type: String = "daily") {
        if (!entityId.startsWith("weather.")) {
            Log.w("EntityStateManager", "Ignoring forecast request for non-weather entity: $entityId")
            return
        }

        HaWebSocketManager.getForecast(entityId, type) { forecast ->
            weatherForecasts[entityId] = forecast
            Log.d("EntityStateManager", "Forecast ($type) received for $entityId with ${forecast.length()} entries")
        }
    }

    fun getForecast(entityId: String): JSONArray? {
        return weatherForecasts[entityId]
    }
}
