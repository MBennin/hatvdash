package com.matthewbennin.hatvdash.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.renderers.SectionsRenderer
// import other renderers as needed
import org.json.JSONObject

object SiftJson {
    @Composable
    fun ExtractViewFromPath(lovelaceJson: JSONObject, path: String) {
        val views = lovelaceJson.optJSONArray("views") ?: return

        for (i in 0 until views.length()) {
            val view = views.getJSONObject(i)
            if (view.optString("path") == path) {
                when (view.optString("type")) {
                    "sections" -> SectionsRenderer.RenderSections(view)
                    "masonry" -> {/* TODO MasonryRenderer.RenderMasonry(view) */}
                    "sidebar" -> {/* TODO SidebarRenderer.RenderSidebar(view) */}
                    "panel" -> {/* TODO PanelRenderer.RenderPanel(view) */}
                    else -> {}
                }
                return
            }
        }
    }

//    fun ExtractDashboardsFromJson(lovelaceJson: JSONObject) {
//        val views = lovelaceJson.optJSONArray("views") ?: return
//
//        var dashboardPanels by remember { mutableStateOf<List<DashboardPanel>>(emptyList()) }
//
//        val dashboards = mutableListOf<DashboardPanel>()
//
//        for (i in 0 until views.length()) {
//            val view = views.getJSONObject(i)
//            dashboards.add(
//                DashboardPanel(
//                    title = view.optString("title", "Unnamed"),
//                    urlPath = view.optString("path", ""),
//                    icon = view.optString("icon", "mdi:view-dashboard")
//                )
//            )
//        }
//
//        dashboardPanels = dashboards.toList()
//
//    }
}
