package com.matthewbennin.hatvdash.ui

import androidx.compose.runtime.Composable
import com.matthewbennin.hatvdash.ui.infoCards.InputSelectInfo
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.ui.infoCards.InputBooleanInfo
import com.matthewbennin.hatvdash.ui.infoCards.InputButtonInfo
import com.matthewbennin.hatvdash.ui.infoCards.InputNumberInfo
import com.matthewbennin.hatvdash.ui.infoCards.LightInfo

@Composable
fun MoreInfoPopup(entityId: String, onDismiss: () -> Unit) {
    val domain = entityId.substringBefore(".")

    when (domain) {
        "input_select" -> InputSelectInfo(entityId, onDismiss)
        "input_number" -> InputNumberInfo(entityId, onDismiss)
        "input_boolean" -> InputBooleanInfo(entityId, onDismiss)
        "input_button" -> InputButtonInfo(entityId, onDismiss)
         "light" -> LightInfo(entityId, onDismiss)
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
