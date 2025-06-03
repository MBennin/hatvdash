package com.matthewbennin.hatvdash.ui.cards

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.json.JSONObject

@Composable
fun EntityCard(cardJson: JSONObject) {
    val name = cardJson.optString("name", "Entity")
    val entityId = cardJson.optString("entity", "unknown.entity")
    Text(text = "EntityCard: $name ($entityId)")
}