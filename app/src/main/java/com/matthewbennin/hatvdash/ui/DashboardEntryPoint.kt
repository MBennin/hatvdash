package com.matthewbennin.hatvdash.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matthewbennin.hatvdash.model.DashboardPanel
import com.matthewbennin.hatvdash.ui.launchscreen.DashboardListScreen
import com.matthewbennin.hatvdash.data.DashboardRepository
import com.matthewbennin.hatvdash.navigation.AppScreen
import com.matthewbennin.hatvdash.ui.dashboardscreen.DashboardViewScreen
import com.matthewbennin.hatvdash.ui.settings.SettingsScreen

@Composable
fun DashboardEntryPoint() {
    var currentScreen by remember { mutableStateOf(AppScreen.DashboardList) }
    var selectedDashboard by remember { mutableStateOf<DashboardPanel?>(null) }
    var dashboardPanels by remember { mutableStateOf<List<DashboardPanel>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        DashboardRepository.loadDashboards(
            onSuccess = { dashboards ->
                dashboardPanels = dashboards
            },
            onError = { message ->
                error = message
            }
        )
    }

    when (currentScreen) {
        AppScreen.DashboardList -> DashboardListScreen(
            onDashboardSelected = {
                selectedDashboard = it
                currentScreen = AppScreen.DashboardView
            },
            onSettingsClicked = {
                currentScreen = AppScreen.Settings
            },
            dashboards = dashboardPanels
        )

        AppScreen.DashboardView -> selectedDashboard?.let {
            DashboardViewScreen(
                dashboard = it,
                onBack = { currentScreen = AppScreen.DashboardList }
            )
        }

        AppScreen.Settings -> SettingsScreen(
            onBack = { currentScreen = AppScreen.DashboardList }
        )
    }

    error?.let {
        Column(Modifier.padding(16.dp)) {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}