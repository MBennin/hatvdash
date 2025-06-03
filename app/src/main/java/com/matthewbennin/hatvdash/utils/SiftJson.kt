package com.matthewbennin.hatvdash.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.matthewbennin.hatvdash.renderers.SectionsRenderer
import org.json.JSONObject

object SiftJson {
    fun extractViewJson(lovelaceJson: JSONObject, path: String): JSONObject? {
        val views = lovelaceJson.optJSONArray("views") ?: return null

        for (i in 0 until views.length()) {
            val view = views.getJSONObject(i)
            if (view.optString("path") == path) {
                return view
            }
        }
        return null
    }

    @Composable
    fun RenderView(viewJson: JSONObject) {
        when (viewJson.optString("type")) {
            "sections" -> SectionsRenderer.RenderSections(viewJson)
            "masonry" -> {/* TODO: MasonryRenderer.RenderMasonry(viewJson) */}
            "sidebar" -> {/* TODO: SidebarRenderer.RenderSidebar(viewJson) */}
            "panel" -> {/* TODO: PanelRenderer.RenderPanel(viewJson) */}
            else -> Text("Unsupported view type: ${viewJson.optString("type")}")
        }
    }
}
