package com.matthewbennin.hatvdash.ui.cards

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.MdiIconManager
import org.json.JSONObject

@Composable
fun ButtonCard(
    name: String,
    entityId: String?,
    mdiIcon: String,
    tapAction: JSONObject?,
    longAction: JSONObject?,
    doubleAction: JSONObject?,
    onAction: (JSONObject?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tintColor = MaterialTheme.colorScheme.primary.toArgb()
    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }


    LaunchedEffect(mdiIcon) {
        MdiIconManager.loadOrFetchIcon(context, mdiIcon, tintColor) {
            iconBitmap = it
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .focusable()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onAction(tapAction) },
                    onDoubleTap = { onAction(doubleAction) },
                    onLongPress = { onAction(longAction) }
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = 100.dp)
                .heightIn(min = 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            iconBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = name,
                    modifier = Modifier.size(64.dp)
                )
            } ?: CircularProgressIndicator(modifier = Modifier.size(24.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ButtonCard(cardJson: JSONObject) {
    val name = cardJson.optString("name", "Unnamed")
    val icon = cardJson.optString("icon", "mdi:alert-circle-outline")
    val entityId = cardJson.optString("entity", null)
    val tapAction = cardJson.optJSONObject("tap_action")
    val longAction = cardJson.optJSONObject("hold_action") ?: cardJson.optJSONObject("long_action")
    val doubleAction = cardJson.optJSONObject("double_action")

    ButtonCard(
        name = name,
        entityId = entityId,
        mdiIcon = icon,
        tapAction = tapAction,
        longAction = longAction,
        doubleAction = doubleAction,
        onAction = { actionJson ->
            // TODO: Hook into real Home Assistant command dispatcher
            println("Executing action: ${actionJson?.toString()}")
        }
    )
}
