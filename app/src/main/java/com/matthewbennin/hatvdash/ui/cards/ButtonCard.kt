package com.matthewbennin.hatvdash.ui.cards

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.logic.RemotePressHandler
import com.matthewbennin.hatvdash.logic.handleInteraction
import com.matthewbennin.hatvdash.ui.rememberRemoteKeyInteractions
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

    val remote = rememberRemoteKeyInteractions(
        onSingleTap = { handleInteraction(context, tapAction, entityId) },
        onDoubleTap = { handleInteraction(context, doubleAction, entityId) },
        onLongPress = {},
        onPostLongPressRelease = { handleInteraction(context, longAction, entityId) }
    )
    val isFocused by remote.isFocused
    val stateJson by remember { derivedStateOf { EntityStateManager.entityStates[entityId] } }

    val state = stateJson?.optString("state") ?: "unknown"
    val attributes = stateJson?.optJSONObject("attributes")
    val domain = entityId?.substringBefore(".") ?: ""

    val defaultPrimaryColor = MaterialTheme.colorScheme.primary

    val iconColor = remember(state, attributes) {
        when {
            state == "unavailable" || state == "unknown" || stateJson == null -> Color(0xFF6F6F6F)

            domain == "light" && state == "on" -> {
                val rgb = attributes?.optJSONArray("rgb_color")
                if (rgb != null && rgb.length() == 3) {
                    Color(
                        red = rgb.getInt(0) / 255f,
                        green = rgb.getInt(1) / 255f,
                        blue = rgb.getInt(2) / 255f
                    )
                } else Color(0xFFFFC107)
            }

            domain == "input_boolean" -> {
                if (state == "on" || state == "true") Color(0xFFFFC107)
                else defaultPrimaryColor
            }

            state == "on" -> Color(0xFFFFC107)

            else -> defaultPrimaryColor
        }
    }

    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(mdiIcon, iconColor) {
        MdiIconManager.loadOrFetchIcon(context, mdiIcon, iconColor.toArgb()) {
            iconBitmap = it
        }
    }

    Surface(
        modifier = modifier
            .padding(4.dp)
            .widthIn(min = 80.dp)
            .heightIn(min = 80.dp)
            .then(remote.modifier),
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

    val domain = entityId?.substringBefore(".") ?: ""

    val toggleableDomains = setOf("light", "input_boolean", "switch", "fan", "climate", "cover")

    // Determine base actions from JSON
    val jsonTap = cardJson.optJSONObject("tap_action")
    val jsonHold = cardJson.optJSONObject("hold_action") ?: cardJson.optJSONObject("long_action")
    val jsonDouble = cardJson.optJSONObject("double_tap_action")

    // Generate default interactions
    val defaultTap = when {
        jsonTap != null -> jsonTap
        toggleableDomains.contains(domain) -> JSONObject().put("action", "toggle").put("entity", entityId)
        else -> JSONObject().put("action", "more-info").put("entity", entityId)
    }

    val finalTap = jsonTap ?: defaultTap
    val finalHold = jsonHold ?: JSONObject().put("action", "more-info").put("entity", entityId)
    val finalDouble = jsonDouble ?: JSONObject(finalTap.toString())

    ButtonCard(
        name = name,
        entityId = entityId,
        mdiIcon = icon,
        tapAction = finalTap,
        longAction = finalHold,
        doubleAction = finalDouble
    )
}
