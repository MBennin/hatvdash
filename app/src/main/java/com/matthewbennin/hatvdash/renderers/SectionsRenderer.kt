package com.matthewbennin.hatvdash.renderers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

object SectionsRenderer {

    @Composable
    fun RenderSections(sectionJson: JSONObject) {
        val sections = sectionJson.optJSONArray("sections") ?: JSONArray()

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in 0 until sections.length()) {
                val section = sections.getJSONObject(i)
                val columnSpan = section.optInt("column_span", 2)
                RenderGrid(section.optJSONArray("cards") ?: JSONArray(), columnSpan)
            }
        }
    }

    @Composable
    fun RenderGrid(cards: JSONArray, columns: Int) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items((0 until cards.length()).map { cards.getJSONObject(it) }) { cardJson ->
                CardRouter.RenderCard(cardJson)
            }
        }
    }
}
