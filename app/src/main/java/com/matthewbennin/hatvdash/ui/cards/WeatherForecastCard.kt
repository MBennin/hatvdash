package com.matthewbennin.hatvdash.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

@Composable
fun WeatherForecastCard(cardJson: JSONObject) {
    val entityId = cardJson.optString("entity", "weather.unknown")
    val showCurrent = cardJson.optBoolean("show_current", true)
    val showForecast = cardJson.optBoolean("show_forecast", true)
    val forecastSlots = cardJson.optInt("forecast_slots", 6)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showCurrent) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⛅", fontSize = 40.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Partly cloudy", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                            Text("Forecast Ladera", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("60°F", fontSize = 22.sp, fontWeight = FontWeight.Medium)
                        Text("73°F / 59°F", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (showForecast) {
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(forecastSlots.coerceIn(1, 7)) { i ->
                        val day = listOf("Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon")[i % 7]
                        val emoji = "☀️"
                        val high = (70..85).random()
                        val low = (55..65).random()
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(day, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(emoji, fontSize = 24.sp)
                            Text("$high°", fontSize = 14.sp)
                            Text("$low°", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}