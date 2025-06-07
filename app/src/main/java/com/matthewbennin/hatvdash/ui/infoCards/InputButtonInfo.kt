package com.matthewbennin.hatvdash.ui.infoCards

import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.network.HaWebSocketManager

@Composable
fun InputButtonInfo(entityId: String, onDismiss: () -> Unit) {
    val state = EntityStateManager.getState(entityId)
    val attrs = state?.optJSONObject("attributes")
    val friendlyName = attrs?.optString("friendly_name", entityId) ?: entityId
    val lastChanged = state?.optString("last_changed")?.replace("T", " ")?.replace("Z", "") ?: "N/A"

    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    when (it.nativeKeyEvent.keyCode) {
                        KEYCODE_DPAD_CENTER -> {
                            HaWebSocketManager.callService(
                                domain = "input_button",
                                service = "press",
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
                    text = "Last pressed: $lastChanged",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2196F3))
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                        .focusable()
                        .onFocusChanged { isFocused = it.isFocused },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PRESS",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
