package com.matthewbennin.hatvdash.ui.dashboardscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matthewbennin.hatvdash.model.DashboardPanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardViewScreen(
    dashboard: DashboardPanel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dashboard.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // TODO: Render the selected dashboard's view
        Text(
            text = "Rendering dashboard: ${dashboard.title}",
            modifier = Modifier.padding(padding)
        )
    }
}