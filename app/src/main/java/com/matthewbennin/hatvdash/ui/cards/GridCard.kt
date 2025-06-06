package com.matthewbennin.hatvdash.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import com.matthewbennin.hatvdash.utils.CardRouter

@Composable
fun GridCard(cardJson: JSONObject) {
    val cards = cardJson.optJSONArray("cards") ?: JSONArray()
    val columns = cardJson.optInt("columns", 2)
    val cardList = List(cards.length()) { i -> cards.getJSONObject(i) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxGridHeight = screenHeight * 0.6f

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxGridHeight)
    ) {
        items(cardList) { childCard ->
            Box(modifier = Modifier.fillMaxWidth()) {
                CardRouter.RenderCard(childCard)
            }
        }
    }
}