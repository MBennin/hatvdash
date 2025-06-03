package com.matthewbennin.hatvdash.ui.launchscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.model.DashboardPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardListScreen(
    dashboards: List<DashboardPanel>,
    onDashboardSelected: (DashboardPanel) -> Unit,
    onSettingsClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select a Dashboard") },
                actions = {
                    IconButton(onClick = onSettingsClicked) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            dashboards.forEach { panel ->
                DashboardCard(
                    title = panel.title,
                    mdiIcon = panel.icon ?: "mdi:view-dashboard",
                    onClick = { onDashboardSelected(panel) }
                )
            }
        }
    }
}