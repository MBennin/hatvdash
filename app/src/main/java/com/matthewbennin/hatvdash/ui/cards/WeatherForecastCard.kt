package com.matthewbennin.hatvdash.ui.cards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caverock.androidsvg.SVG
import com.matthewbennin.hatvdash.data.EntityStateManager
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherForecastCard(cardJson: JSONObject) {
    val entityId = cardJson.optString("entity", "weather.unknown")
    val forecastType = cardJson.optString("forecast_type", "daily")
    val showCurrent = cardJson.optBoolean("show_current", true)
    val showForecast = cardJson.optBoolean("show_forecast", true)
    val forecastSlots = cardJson.optInt("forecast_slots", 6)

    val context = LocalContext.current
    val tintColor = MaterialTheme.colorScheme.primary.toArgb()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    EntityStateManager.trackEntity(entityId)

    LaunchedEffect(entityId, forecastType) {
        EntityStateManager.requestForecastForEntity(entityId, forecastType)
    }

    val forecastArray = EntityStateManager.getForecast(entityId)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                    it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    Toast.makeText(context, "Forecast for ${entityId.substringAfter(".")}", Toast.LENGTH_SHORT).show()
                    true
                } else false
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            if (showCurrent && forecastArray != null && forecastArray.length() > 0) {
                val current = forecastArray.getJSONObject(0)
                val condition = current.optString("condition", "unknown")
                val temp = current.optInt("temperature", 0)
                val low = current.optInt("templow", temp)

                var iconBitmap by remember(condition) { mutableStateOf<Bitmap?>(null) }
                LaunchedEffect(condition) {
                    loadWeatherIcon(context, condition) {
                        iconBitmap = it
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        iconBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = condition,
                                modifier = Modifier.size(40.dp)
                            )
                        } ?: Text("…", fontSize = 40.sp)

                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                condition.replace("-", ", ").replaceFirstChar(Char::uppercase),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Forecast ${entityId.substringAfter(".").replaceFirstChar(Char::uppercase)}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("$temp °F", fontSize = 22.sp, fontWeight = FontWeight.Medium)
                        Text("$temp °F / $low °F", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (showForecast && forecastArray != null) {
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until minOf(forecastSlots, forecastArray.length())) {
                        val item = forecastArray.getJSONObject(i)
                        val condition = item.optString("condition", "unknown")
                        val dateStr = item.optString("datetime", "")

                        val day = try {
                            val inputDate = dateStr.substring(0, 10)
                            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formatter = SimpleDateFormat("EEE", Locale.getDefault())
                            val parsedDate = parser.parse(inputDate)
                            formatter.format(parsedDate ?: Date())
                        } catch (e: Exception) { "???" }

                        val temp = item.optInt("temperature", 0)
                        val low = item.optInt("templow", temp)

                        var iconBitmap by remember(condition) { mutableStateOf<Bitmap?>(null) }
                        LaunchedEffect(condition) {
                            loadWeatherIcon(context, condition) {
                                iconBitmap = it
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(day, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            iconBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = condition,
                                    modifier = Modifier.size(28.dp)
                                )
                            } ?: Text("…", fontSize = 24.sp)
                            Text("$temp°", fontSize = 14.sp)
                            Text("$low°", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (forecastArray == null) {
                Spacer(Modifier.height(8.dp))
                Text("Loading forecast...", fontSize = 14.sp)
            }
        }
    }
}

private fun loadWeatherIcon(
    context: Context,
    condition: String,
    onReady: (Bitmap?) -> Unit
) {
    val fileName = if (fileExists(context, "weather/$condition.svg")) {
        "$condition.svg"
    } else {
        Log.w("WeatherIcon", "Missing icon for condition: $condition, using fallback")
        "cloudy.svg"
    }

    try {
        val input = context.assets.open("weather/$fileName")
        val svg = SVG.getFromInputStream(input)
        svg.setDocumentWidth("48px")
        svg.setDocumentHeight("48px")

        val picture: Picture = svg.renderToPicture()
        val bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPicture(picture)

        onReady(bitmap)
    } catch (e: Exception) {
        Log.e("WeatherIcon", "Failed to load icon: $fileName", e)
        onReady(null)
    }
}

private fun fileExists(context: Context, path: String): Boolean {
    return try {
        context.assets.open(path).close()
        true
    } catch (e: Exception) {
        false
    }
}
