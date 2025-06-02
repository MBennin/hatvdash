package com.matthewbennin.hatvdash

import androidx.compose.runtime.Composable
import org.json.JSONArray
import org.json.JSONObject

object LovelaceParser {

    @Composable
    fun RenderSection(section: JSONObject) {
        val cards = section.optJSONArray("cards") ?: JSONArray()
        val sections = section.optJSONArray("sections") ?: JSONArray()
        val columnSpan = section.optInt("column_span", 1)

        if (sections.length() > 0) {
            RenderGrid(sections, columnSpan)
        } else {
            RenderGrid(cards, columnSpan)
        }
    }

    @Composable
    fun RenderGrid(items: JSONArray, columns: Int) {
        // Uses LazyVerticalGrid or manual layout
    }

    @Composable
    fun RenderCard(card: JSONObject) {
        // Dispatch to ButtonCard, EntityCard, etc.
    }
}
