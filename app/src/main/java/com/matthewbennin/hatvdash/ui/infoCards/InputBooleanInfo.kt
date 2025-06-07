package com.matthewbennin.hatvdash.ui.infoCards

import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_BACK
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import com.matthewbennin.hatvdash.MdiIconManager
import org.json.JSONObject

@Composable
fun InputBooleanInfo(entityId: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes") ?: JSONObject()
    val currentState = state?.optString("state") ?: "off"
    val friendlyName = attrs.optString("friendly_name", entityId)

    val isOn = remember(currentState) { currentState == "on" }

    var isFocused by remember { mutableStateOf(false) }
    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load MDI icon dynamically
    LaunchedEffect(isOn) {
        val tint = if (isOn) Color.Black.toArgb() else Color.White.toArgb()
        MdiIconManager.loadOrFetchIcon(context, "mdi:power", tint) {
            iconBitmap = it
        }
    }

    Box(
        modifier = Modifier
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    when (it.nativeKeyEvent.keyCode) {
                        KEYCODE_DPAD_CENTER -> {
                            val newState = if (isOn) "off" else "on"
                            HaWebSocketManager.callService(
                                domain = "input_boolean",
                                service = if (newState == "on") "turn_on" else "turn_off",
                                entityId = entityId
                            )
                            onDismiss()
                            true
                        }

                        KEYCODE_BACK -> {
                            onDismiss()
                            true
                        }

                        else -> false
                    }
                } else false
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
                    text = friendlyName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(12.dp))

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
                        .focusable()
                        .onFocusChanged { isFocused = it.isFocused },
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
}
