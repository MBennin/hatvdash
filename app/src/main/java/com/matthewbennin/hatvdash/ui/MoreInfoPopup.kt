package com.matthewbennin.hatvdash.ui

import androidx.compose.runtime.Composable
import com.matthewbennin.hatvdash.ui.infoCards.InputSelectInfo
import com.matthewbennin.hatvdash.data.EntityStateManager

// import other infoCards as you add them, e.g.:
// import com.mattbennin.hatvdash.ui.infoCards.LightInfo

@Composable
fun MoreInfoPopup(entityId: String, onDismiss: () -> Unit) {
    val domain = entityId.substringBefore(".")

    when (domain) {
        "input_select" -> InputSelectInfo(entityId, onDismiss)
        // "light" -> LightInfo(entityId, onDismiss)
        // "sensor" -> SensorInfo(entityId, onDismiss)
        else -> FallbackInfo(entityId, onDismiss)
    }
}

@Composable
private fun FallbackInfo(entityId: String, onDismiss: () -> Unit) {
    val state = EntityStateManager.getState(entityId)
    val stateText = state?.optString("state") ?: "unknown"

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            androidx.compose.material3.Text(text = entityId)
        },
        text = {
            androidx.compose.material3.Text(text = "State: $stateText")
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Close")
            }
        }
    )
}
