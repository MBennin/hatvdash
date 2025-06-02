package com.matthewbennin.hatvdash.renderers

import androidx.compose.runtime.Composable
import com.matthewbennin.hatvdash.ui.cards.ButtonCard
import org.json.JSONObject

object CardRouter {

    @Composable
    fun RenderCard(cardJson: JSONObject) {
        when (cardJson.optString("type")) {
            "button" -> ButtonCard(cardJson)

            // TODO: Add more renderers
            // "entity" -> EntityCardRenderer.Render(cardJson)
            // "grid" -> GridRenderer.Render(cardJson)
            // "vertical-stack" -> VerticalStackRenderer.Render(cardJson)
            // etc.

            else -> {
                // Optional: Render a placeholder or log unsupported type
                // Text("Unsupported card type: ${cardJson.optString("type")}")
            }
        }
    }
}