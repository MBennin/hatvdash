package com.matthewbennin.hatvdash.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import com.matthewbennin.hatvdash.utils.CardRouter

@Composable
fun HorizontalStackCard(cardJson: JSONObject) {
    val cards = cardJson.optJSONArray("cards") ?: JSONArray()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until cards.length()) {
            val childCard = cards.getJSONObject(i)
            CardRouter.RenderCard(childCard)
        }
    }
}