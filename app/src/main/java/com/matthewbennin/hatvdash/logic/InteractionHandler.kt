package com.matthewbennin.hatvdash.logic

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.matthewbennin.hatvdash.ui.PopupStateManager
import org.json.JSONObject

fun handleInteraction(context: Context, action: JSONObject?, entityId: String?, onMoreInfo: ((entityId: String) -> Unit)? = null) {
    Log.d("handleInteraction", "action: $action")
    val type = action?.optString("action") ?: "none"

    when (type) {
        "toggle" -> {
            // Send toggle service via HaWebSocketManager
            Toast.makeText(context, "Toggle $entityId", Toast.LENGTH_SHORT).show()
        }

        "navigate" -> {
            val path = action?.optString("navigation_path")
            Toast.makeText(context, "Navigate to $path", Toast.LENGTH_SHORT).show()
        }

        "url" -> {
            val url = action?.optString("url")
            Toast.makeText(context, "Open URL: $url", Toast.LENGTH_SHORT).show()
        }

        "more-info" -> {
            PopupStateManager.show(entityId ?: "")
        }

        "call-service", "perform-action", "assist" -> {
            Toast.makeText(context, "Action: $type not yet implemented", Toast.LENGTH_SHORT).show()
        }

        "none", "nothing" -> {
            // Do nothing
        }

        else -> {
            Toast.makeText(context, "Default fallback action for $entityId", Toast.LENGTH_SHORT).show()
        }
    }
}
