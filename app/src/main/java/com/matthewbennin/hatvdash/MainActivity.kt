package com.matthewbennin.hatvdash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import com.matthewbennin.hatvdash.network.HomeAssistantConfig
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect to WebSocket on startup
        HaWebSocketManager.connect(
            baseUrl = HomeAssistantConfig.BASE_URL,
            token = HomeAssistantConfig.TOKEN
        )

        setContent {
            MaterialTheme {
                var dashboardTitles by remember { mutableStateOf<List<String>>(emptyList()) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    HaWebSocketManager.requestLovelaceJson { config ->
                        val views = config.optJSONArray("views") ?: JSONArray()
                        val titles = mutableListOf<String>()
                        for (i in 0 until views.length()) {
                            titles.add(views.getJSONObject(i).optString("title", "Unnamed"))
                        }

                        runOnUiThread {
                            println("Updating dashboardTitles with ${titles.size} items")
                            dashboardTitles = titles.toList()
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Dashboard Fetcher") })
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(16.dp)
                            .fillMaxSize()
                    ) {
                        if (dashboardTitles.isEmpty()) {
                            Text("Loading dashboards...")
                        } else {
                            Text("Loaded ${dashboardTitles.size} dashboards")
                            dashboardTitles.forEach { title ->
                                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                            }
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
