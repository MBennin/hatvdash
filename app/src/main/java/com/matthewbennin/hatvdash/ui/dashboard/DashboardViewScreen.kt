package com.matthewbennin.hatvdash.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.matthewbennin.hatvdash.data.EntityStateManager
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.ui.MoreInfoPopup
import com.matthewbennin.hatvdash.ui.PopupStateManager
import com.matthewbennin.hatvdash.utils.SiftJson
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardViewScreen(
    dashboard: DashboardPanel,
    lovelaceJson: JSONObject,
    onBack: () -> Unit
) {

    val entityId = PopupStateManager.moreInfoEntityId.value

    if (entityId != null) {
        Dialog(onDismissRequest = { PopupStateManager.dismiss() }) {
            MoreInfoPopup(
                entityId = entityId,
                onDismiss = { PopupStateManager.dismiss() }
            )
        }
    }
    // 🧼 Prune unused states when this screen is entered
    LaunchedEffect(Unit) {
        EntityStateManager.pruneUntrackedStates()
    }

    Scaffold { padding ->
        val viewJson = SiftJson.extractViewJson(lovelaceJson, dashboard.urlPath)
        if (viewJson != null) {
            Surface(modifier = Modifier.padding(padding)) {
                SiftJson.RenderView(viewJson)
            }
        } else {
            Text(
                text = "Error: View not found",
                modifier = Modifier.padding(padding)
            )
        }
    }
}
