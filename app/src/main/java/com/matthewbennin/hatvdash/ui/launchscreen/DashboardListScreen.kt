package com.matthewbennin.hatvdash.ui.launchscreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import com.matthewbennin.hatvdash.model.DashboardPanel

@Composable
fun DashboardListScreen(
    dashboards: List<DashboardPanel>,
    onDashboardSelected: (DashboardPanel) -> Unit
) {

    LazyColumn {
        itemsIndexed(dashboards) { index, dashboard ->
            DashboardCard(
                title = dashboard.title,
                mdiIcon = dashboard.icon ?: "mdi:view-dashboard",
                onClick = { onDashboardSelected(dashboard) }
            )
        }
    }

    // Optional: Add D-pad key events to shift `selectedIndex`
}
