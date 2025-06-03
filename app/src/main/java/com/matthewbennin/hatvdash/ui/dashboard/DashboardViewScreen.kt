package com.matthewbennin.hatvdash.ui.dashboardscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.utils.SiftJson
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardViewScreen(
    dashboard: DashboardPanel,
    lovelaceJson: JSONObject,
    onBack: () -> Unit
) {
    Scaffold(
    ) { padding ->
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