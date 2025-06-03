package com.matthewbennin.hatvdash.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.json.JSONArray
import org.json.JSONObject
import com.matthewbennin.hatvdash.utils.CardRouter

@Composable
fun VerticalStackCard(cardJson: JSONObject) {
    val cards = cardJson.optJSONArray("cards") ?: JSONArray()
    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until cards.length()) {
            val childCard = cards.getJSONObject(i)
            CardRouter.RenderCard(childCard)
        }
    }
}