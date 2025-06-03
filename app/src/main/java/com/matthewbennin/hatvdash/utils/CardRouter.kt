package com.matthewbennin.hatvdash.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.matthewbennin.hatvdash.ui.cards.ButtonCard
import com.matthewbennin.hatvdash.ui.cards.EntityCard
import com.matthewbennin.hatvdash.ui.cards.GridCard
import com.matthewbennin.hatvdash.ui.cards.HorizontalStackCard
import com.matthewbennin.hatvdash.ui.cards.VerticalStackCard
import com.matthewbennin.hatvdash.ui.cards.WeatherForecastCard
import org.json.JSONObject

object CardRouter {

    @Composable
    fun RenderCard(cardJson: JSONObject) {
        when (cardJson.optString("type")) {
            "button" -> ButtonCard(cardJson)
            "entity" -> EntityCard(cardJson)
            "vertical-stack" -> VerticalStackCard(cardJson)
            "horizontal-stack" -> HorizontalStackCard(cardJson)
            "grid" -> GridCard(cardJson)
            "weather-forecast" -> WeatherForecastCard(cardJson)
            else -> Text("Unsupported card type: ${cardJson.optString("type")}")
        }
    }
}
