package com.matthewbennin.hatvdash.ui.cards

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matthewbennin.hatvdash.MdiIconManager
import com.matthewbennin.hatvdash.data.EntityStateManager
import org.json.JSONObject

@Composable
fun EntityCard(cardJson: JSONObject) {
    val context = LocalContext.current

    val entityId = cardJson.optString("entity", null)

    // Track entity for state/icon fallback
    if (!entityId.isNullOrBlank()) {
        EntityStateManager.trackEntity(entityId)
    }

    val stateJson = entityId?.let { EntityStateManager.stateMap[it] }
    val attributes = stateJson?.optJSONObject("attributes")

    val fallbackName = attributes?.optString("friendly_name")
    val fallbackIcon = attributes?.optString("icon")
    val rawIcon = cardJson.optString("icon")
    val name = cardJson.optString("name").ifBlank { fallbackName ?: "Entity" }
    val icon = if (!rawIcon.isNullOrBlank()) rawIcon else fallbackIcon ?: "mdi:alert-circle-outline"
    val state = stateJson?.optString("state") ?: "..."

    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val tintColor = MaterialTheme.colorScheme.primary.toArgb()

    LaunchedEffect(icon) {
        MdiIconManager.loadOrFetchIcon(context, icon, tintColor) {
            iconBitmap = it
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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
