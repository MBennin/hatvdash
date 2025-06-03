package com.matthewbennin.hatvdash.data
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import org.json.JSONArray
import org.json.JSONObject

object DashboardRepository {

    fun loadDashboards(
        onSuccess: (List<DashboardPanel>, JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        HaWebSocketManager.requestLovelaceJson { config ->
            try {
                val views = config.optJSONArray("views") ?: JSONArray()
                val dashboards = List(views.length()) { i ->
                    val view = views.getJSONObject(i)
                    DashboardPanel(
                        title = view.optString("title", "Unnamed"),
                        urlPath = view.optString("path", ""),
                        icon = view.optString("icon", "mdi:view-dashboard")
                    )
                }
                onSuccess(dashboards, config)
            } catch (e: Exception) {
                onError("Failed to parse Lovelace JSON: ${e.message}")
            }
        }
    }
}

