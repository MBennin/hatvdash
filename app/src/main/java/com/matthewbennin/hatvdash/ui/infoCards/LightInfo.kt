package com.matthewbennin.hatvdash.ui.infoCards

import android.graphics.Bitmap
import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import com.matthewbennin.hatvdash.ui.rememberMdiIconBitmap
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun LightInfo(entityId: String, onDismiss: () -> Unit) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: return

    val name = attrs.optString("friendly_name", entityId)
    val availableControls = listOfNotNull(
        "on",
        if (attrs.has("brightness")) "brightness" else null,
        if (attrs.has("color_temp")) "color_temp" else null,
        if (attrs.has("rgb_color")) "rgb" else null,
        if (attrs.has("effect_list")) "effect" else null
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .focusable(),
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .focusGroup(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown) {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_LEFT -> {
                                    selectedIndex = (selectedIndex - 1 + availableControls.size) % availableControls.size
                                    true
                                }

                                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                    selectedIndex = (selectedIndex + 1) % availableControls.size
                                    true
                                }

                                else -> false
                            }
                        } else false
                    }
                    .focusable()
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (availableControls.getOrNull(selectedIndex)) {
                    "on" -> LightToggleControl(entityId, onDismiss)
                    "brightness" -> LightBrightnessControl(entityId)
                    "color_temp" -> LightColorTempControl(entityId)
                    "rgb" -> LightColorControl(entityId)
                    "effect" -> LightEffectControl(entityId)
                }
            }
        }
    }
}


@Composable
fun LightToggleControl(entityId: String, onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: JSONObject()
    val currentState = state?.optString("state") ?: "off"
    val friendlyName = attrs.optString("friendly_name", entityId)

    val isOn = remember(currentState) { currentState == "on" }

    var isFocused by remember { mutableStateOf(false) }
    val toggleFocus = remember { FocusRequester() }

    val tintColor = remember(isOn) { if (isOn) Color.Black.toArgb() else Color.White.toArgb() }
    val iconBitmap by rememberMdiIconBitmap("mdi:power", tintColor)


    LaunchedEffect(Unit) {
        delay(100)
        toggleFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .focusRequester(toggleFocus)
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            HaWebSocketManager.callService(
                                domain = "light",
                                service = if (isOn) "turn_off" else "turn_on",
                                entityId = entityId
                            )
                            onDismiss()
                            true
                        }
                        KeyEvent.KEYCODE_BACK -> {
                            onDismiss()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusable()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isOn) "On" else "Off",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 200.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isOn) Color(0xFFFFC107) else Color(0xFF424242))
                    .onFocusChanged { isFocused = it.isFocused }
                    .focusable(),
                contentAlignment = Alignment.Center
            ) {
                iconBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Power icon",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LightBrightnessControl(entityId: String) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: JSONObject()

    val currentBrightness = attrs.optInt("brightness", 0)
    val brightnessPercent = remember(currentBrightness) {
        ((currentBrightness / 255.0) * 100).toInt().coerceIn(0, 100)
    }

    var sliderValue by remember { mutableStateOf(brightnessPercent) }
    val lastChanged = state?.optString("last_changed") ?: "?"

    val fillFraction = sliderValue / 100f
    val fillHeight = 200f * fillFraction

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            sliderValue = (sliderValue + 5).coerceAtMost(100)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            sliderValue = (sliderValue - 5).coerceAtLeast(0)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            val haValue = ((sliderValue / 100.0) * 255).toInt()
                            HaWebSocketManager.callService(
                                domain = "light",
                                service = "turn_on",
                                entityId = entityId,
                                data = JSONObject().put("brightness", haValue)
                            )
                            true
                        }

                        else -> false
                    }
                } else false
            }
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$sliderValue%",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 200.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF444444)), // dark outer shell
                contentAlignment = Alignment.BottomCenter
            ) {
                // Yellow fill bar rising from bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = fillFraction)
                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                        .background(Color(0xFFFFC107))
                        .align(Alignment.BottomCenter)
                )

                // White drag handle positioned at fill height
                Box(
                    modifier = Modifier
                        .offset(y = -(fillHeight.dp - 8.dp))
                        .width(36.dp)
                        .height(4.dp)
                        .background(Color.White, shape = RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Composable
fun LightColorTempControl(entityId: String) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: return

    val currentMireds = attrs.optInt("color_temp", 0)
    val minMireds = attrs.optInt("min_mireds", 153)
    val maxMireds = attrs.optInt("max_mireds", 500)
    val range = maxMireds - minMireds

    val currentPercent = remember(currentMireds) {
        ((currentMireds - minMireds) / range.toFloat() * 100).toInt().coerceIn(0, 100)
    }

    var sliderValue by remember { mutableStateOf(currentPercent) }
    val fillFraction = sliderValue / 100f
    val fillHeight = 200f * fillFraction

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Color Temp",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                sliderValue = (sliderValue + 5).coerceAtMost(100)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                sliderValue = (sliderValue - 5).coerceAtLeast(0)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                val mired = (minMireds + (range * sliderValue / 100.0)).toInt()
                                HaWebSocketManager.callService(
                                    domain = "light",
                                    service = "turn_on",
                                    entityId = entityId,
                                    data = JSONObject().put("color_temp", mired)
                                )
                                true
                            }

                            else -> false
                        }
                    } else false
                }
                .focusable()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 200.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFA726), // warm orange top
                                    Color(0xFFFFFFFF)  // cool white bottom
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = -(fillHeight.dp - 8.dp))
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color.White, shape = RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}



@Composable
fun LightColorControl(entityId: String) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: return

    val rgb = attrs.optJSONArray("rgb_color")
    val initialHue = if (rgb != null && rgb.length() == 3) {
        val r = rgb.optInt(0)
        val g = rgb.optInt(1)
        val b = rgb.optInt(2)
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(r, g, b, hsv)
        hsv[0]
    } else {
        0f
    }

    var sliderValue by remember { mutableFloatStateOf(initialHue) }
    val fillFraction = sliderValue / 360f
    val fillHeight = 200f * fillFraction

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Color",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                sliderValue = (sliderValue + 10f).coerceAtMost(360f)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                sliderValue = (sliderValue - 10f).coerceAtLeast(0f)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                val hsv = floatArrayOf(sliderValue, 1f, 1f)
                                val colorInt = android.graphics.Color.HSVToColor(hsv)
                                val r = (colorInt shr 16) and 0xFF
                                val g = (colorInt shr 8) and 0xFF
                                val b = colorInt and 0xFF

                                HaWebSocketManager.callService(
                                    domain = "light",
                                    service = "turn_on",
                                    entityId = entityId,
                                    data = JSONObject().put("rgb_color", JSONArray().put(r).put(g).put(b))
                                )
                                true
                            }

                            else -> false
                        }
                    } else false
                }
                .focusable()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 200.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = List(13) { i ->
                                    Color.hsv((i * 30f) % 360f, 1f, 1f)
                                }.reversed()
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = -(fillHeight.dp - 8.dp))
                            .width(36.dp)
                            .height(4.dp)
                            .background(Color.White, shape = RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}


@Composable
fun LightEffectControl(entityId: String) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: return

    val currentEffect = state.optString("effect", "")
    val effectsArray = attrs.optJSONArray("effect_list") ?: return
    val effects = List(effectsArray.length()) { effectsArray.getString(it) }

    var selectedIndex by remember { mutableIntStateOf(effects.indexOf(currentEffect).coerceAtLeast(0)) }

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        listState.scrollToItem(selectedIndex)
    }

    // This ensures the list scrolls every time selectedIndex changes
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            selectedIndex = (selectedIndex - 1 + effects.size) % effects.size
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            selectedIndex = (selectedIndex + 1) % effects.size
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER -> {
                            HaWebSocketManager.callService(
                                domain = "light",
                                service = "turn_on",
                                entityId = entityId,
                                data = JSONObject().put("effect", effects[selectedIndex])
                            )
                            true
                        }

                        else -> false
                    }
                } else false
            }
            .focusable()
            .fillMaxWidth()
            .height(300.dp) // display height for scrollable list
    ) {
        Text(
            text = "Select Effect",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(effects) { index, effect ->
                Text(
                    text = effect,
                    fontSize = 16.sp,
                    color = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically),
                )
            }
        }
    }
}



