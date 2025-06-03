package com.matthewbennin.hatvdash.renderers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false // prevent user scroll if everything fits
        ) {
            items(sections.length()) { i ->
                val section = sections.getJSONObject(i)
                RenderGridSection(section)
            }
        }
    }

    @Composable
    fun RenderGridSection(sectionJson: JSONObject) {
        val cards = sectionJson.optJSONArray("cards") ?: JSONArray()
        val columnSpan = sectionJson.optInt("column_span", 2)

        val cardList = List(cards.length()) { i -> cards.getJSONObject(i) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnSpan),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)
        ) {
            items(cardList) { cardJson ->
                CardRouter.RenderCard(cardJson)
            }
        }
    }
}
