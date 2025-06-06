package com.matthewbennin.hatvdash.ui.cards

import android.graphics.Bitmap
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
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.logic.RemotePressHandler
import com.matthewbennin.hatvdash.logic.handleInteraction
import org.json.JSONObject

@Composable
fun ButtonCard(
    name: String,
    entityId: String?,
    mdiIcon: String,
    tapAction: JSONObject?,
    longAction: JSONObject?,
    doubleAction: JSONObject?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tintColor = MaterialTheme.colorScheme.primary.toArgb()
    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(mdiIcon) {
        MdiIconManager.loadOrFetchIcon(context, mdiIcon, tintColor) {
            iconBitmap = it
        }
    }

    Surface(
        modifier = modifier
            .padding(4.dp)
            .widthIn(min = 80.dp)
            .heightIn(min = 80.dp)
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource)
            .onKeyEvent { event ->
                val key = event.nativeKeyEvent
                if (key.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || key.keyCode == KeyEvent.KEYCODE_ENTER) {
                    RemotePressHandler.handleKeyEvent(
                        event = key,
                        onSingleTap = { handleInteraction(context, tapAction, entityId) },
                        onDoubleTap = { handleInteraction(context, doubleAction, entityId) },
                        onLongPress = { handleInteraction(context, longAction, entityId) }
                    )
                    true
                } else false
            },
        shape = RoundedCornerShape(16.dp),
        color = if (isFocused)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        tonalElevation = if (isFocused) 4.dp else 0.dp,
        shadowElevation = if (isFocused) 6.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            iconBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = name,
                    modifier = Modifier.size(48.dp)
                )
            } ?: Spacer(modifier = Modifier.size(48.dp))

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ButtonCard(cardJson: JSONObject) {
    val entityId = cardJson.optString("entity", null)

    if (!entityId.isNullOrBlank()) {
        EntityStateManager.trackEntity(entityId, needsIcon = cardJson.optString("icon").isNullOrBlank())
    }

    val fallbackName = EntityStateManager.getFriendlyNameForEntity(entityId ?: "")
    val name = cardJson.optString("name").ifBlank { fallbackName ?: "Unnamed" }

    val rawIcon = cardJson.optString("icon")
    val fallbackIcon = EntityStateManager.getIconForEntity(entityId ?: "")
    val icon = when {
        !rawIcon.isNullOrBlank() -> rawIcon
        !fallbackIcon.isNullOrBlank() -> fallbackIcon
        else -> "mdi:alert-circle-outline"
    }

    val tapAction = cardJson.optJSONObject("tap_action")
    val longAction = cardJson.optJSONObject("hold_action") ?: cardJson.optJSONObject("long_action")
    val doubleAction = cardJson.optJSONObject("double_tap_action")

    ButtonCard(
        name = name,
        entityId = entityId,
        mdiIcon = icon,
        tapAction = tapAction,
        longAction = longAction,
        doubleAction = doubleAction
    )
}
