package com.matthewbennin.hatvdash.renderers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.utils.CardRouter
import org.json.JSONArray
import org.json.JSONObject

object SectionsRenderer {

    @Composable
    fun RenderSections(viewJson: JSONObject) {
        val sections = viewJson.optJSONArray("sections") ?: JSONArray()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until sections.length()) {
                val section = sections.getJSONObject(i)
                RenderGridSection(section)
            }
        }
    }

    @Composable
    fun RenderGridSection(sectionJson: JSONObject) {
        val cards = sectionJson.optJSONArray("cards") ?: JSONArray()
        val columnSpan = sectionJson.optInt("column_span", 2)

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnSpan),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items((0 until cards.length()).map { cards.getJSONObject(it) }) { cardJson ->
                CardRouter.RenderCard(cardJson)
            }
        }
    }
}
