package com.matthewbennin.hatvdash.ui.cards

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
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
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.logic.RemotePressHandler
import com.matthewbennin.hatvdash.logic.handleInteraction
import org.json.JSONObject

@Composable
fun EntityCard(cardJson: JSONObject) {
    val context = LocalContext.current
    val entityId = cardJson.optString("entity", null)

    if (!entityId.isNullOrBlank()) {
        EntityStateManager.trackEntity(entityId)
    }

    // Observe just this entity’s state
    val stateJson by remember {
        derivedStateOf { EntityStateManager.entityStates[entityId] }
    }

    val attributes = stateJson?.optJSONObject("attributes")
    val fallbackName = attributes?.optString("friendly_name")
    val fallbackIcon = attributes?.optString("icon")
    val rawIcon = cardJson.optString("icon")
    val name = cardJson.optString("name").ifBlank { fallbackName ?: "Entity" }
    val icon = if (!rawIcon.isNullOrBlank()) rawIcon else fallbackIcon ?: "mdi:alert-circle-outline"
    val state = stateJson?.optString("state") ?: "..."

    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val tintColor = MaterialTheme.colorScheme.primary.toArgb()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(icon) {
        MdiIconManager.loadOrFetchIcon(context, icon, tintColor) {
            iconBitmap = it
        }
    }

    // --- Interaction Defaults ---
    fun getActionOrDefault(key: String): JSONObject? {
        return cardJson.optJSONObject(key) ?: JSONObject().put("action", "more-info").put("entity", entityId)
    }

    val tapAction = getActionOrDefault("tap_action")
    val doubleTapAction = cardJson.optJSONObject("double_tap_action") ?: tapAction
    val holdAction = getActionOrDefault("hold_action")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .onKeyEvent { event ->
                val key = event.nativeKeyEvent
                if (key.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || key.keyCode == KeyEvent.KEYCODE_ENTER) {
                    RemotePressHandler.handleKeyEvent(
                        event = key,
                        onSingleTap = { handleInteraction(context, tapAction, entityId) },
                        onDoubleTap = { handleInteraction(context, doubleTapAction, entityId) },
                        onLongPress = { /* intentionally empty */ },
                        onPostLongPressRelease = {
                            handleInteraction(context, holdAction, entityId)
                        }
                    )
                    true
                } else {
                    false
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                iconBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = name,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
