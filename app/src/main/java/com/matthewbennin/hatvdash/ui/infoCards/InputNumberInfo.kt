package com.matthewbennin.hatvdash.ui.infoCards

import android.view.KeyEvent
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import org.json.JSONObject
import kotlinx.coroutines.delay

@Composable
fun InputNumberInfo(entityId: String, onDismiss: () -> Unit) {
    val state = EntityStateManager.entityStates[entityId]
    val attrs = state?.optJSONObject("attributes") ?: JSONObject()

    val name = attrs.optString("friendly_name", entityId)
    val min = attrs.optDouble("min", 0.0)
    val max = attrs.optDouble("max", 100.0)
    val step = attrs.optDouble("step", 1.0)
    val current = state?.optDouble("state", min) ?: min

    var sliderValue by remember { mutableStateOf(current.toFloat()) }

    val sliderHeldDirection = remember { mutableStateOf(0) } // -1 = left, 1 = right, 0 = idle
    val sliderRepeatDelay = remember { mutableStateOf(300L) }

    // Accelerated repeat logic
    LaunchedEffect(sliderHeldDirection.value) {
        if (sliderHeldDirection.value != 0) {
            while (sliderHeldDirection.value != 0) {
                when (sliderHeldDirection.value) {
                    -1 -> sliderValue = maxOf(sliderValue - step.toFloat(), min.toFloat())
                    1 -> sliderValue = minOf(sliderValue + step.toFloat(), max.toFloat())
                }
                delay(sliderRepeatDelay.value)
                sliderRepeatDelay.value = maxOf(50L, (sliderRepeatDelay.value * 0.9).toLong())
            }
            sliderRepeatDelay.value = 300L // reset on release
        }
    }

    Box(
        modifier = Modifier
            .onKeyEvent { event ->
                when (event.type) {
                    KeyEventType.KeyDown -> {
                        when (event.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                if (sliderHeldDirection.value == 0) sliderHeldDirection.value = -1
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                if (sliderHeldDirection.value == 0) sliderHeldDirection.value = 1
                                true
                            }

                            else -> false
                        }
                    }

                    KeyEventType.KeyUp -> {
                        when (event.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                sliderHeldDirection.value = 0
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                HaWebSocketManager.callService(
                                    domain = "input_number",
                                    service = "set_value",
                                    entityId = entityId,
                                    data = JSONObject().put("value", sliderValue)
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
                    }

                    else -> false
                }
            }
            .focusable()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(32.dp)
                .focusable(),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Current: ${sliderValue.toInt()}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = min.toFloat()..max.toFloat(),
                    steps = ((max - min) / step).toInt() - 1,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Press OK to confirm, Back to cancel",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
