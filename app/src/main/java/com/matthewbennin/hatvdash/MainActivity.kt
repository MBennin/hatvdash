package com.matthewbennin.hatvdash

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import com.matthewbennin.hatvdash.network.HomeAssistantConfig
import com.matthewbennin.hatvdash.ui.launchscreen.DashboardListScreen
import com.matthewbennin.hatvdash.ui.theme.HATVDashTheme
import com.matthewbennin.hatvdash.MdiIconManager
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HaWebSocketManager.connect(
            baseUrl = HomeAssistantConfig.BASE_URL,
            token = HomeAssistantConfig.TOKEN
        )


        setContent {
            HATVDashTheme(isInDarkTheme = false) {
                var dashboardPanels by remember { mutableStateOf<List<DashboardPanel>>(emptyList()) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    HaWebSocketManager.requestLovelaceJson { config ->
                        val views = config.optJSONArray("views") ?: JSONArray()
                        val dashboards = mutableListOf<DashboardPanel>()

                        for (i in 0 until views.length()) {
                            val view = views.getJSONObject(i)
                            dashboards.add(
                                DashboardPanel(
                                    title = view.optString("title", "Unnamed"),
                                    urlPath = view.optString("path", ""),
                                    icon = view.optString("icon", "mdi:view-dashboard")
                                )
                            )
                        }

                        val context = this@MainActivity
                        val iconMap = mutableMapOf<String, Bitmap?>()

                        dashboardPanels = dashboards.toList()

                        runOnUiThread {
                            dashboardPanels = dashboards.toList()
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Select a Dashboard") })
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        if (dashboardPanels.isEmpty()) {
                            Text("Loading dashboards...")
                        } else {
                            DashboardListScreen(
                                dashboards = dashboardPanels,
                                onDashboardSelected = { selected ->
                                    // TODO: Save selected dashboard and render it
                                }
                            )
                        }

                        error?.let {
                            Spacer(Modifier.height(16.dp))
                            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
