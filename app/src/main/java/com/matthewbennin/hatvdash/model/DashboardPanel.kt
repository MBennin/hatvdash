package com.matthewbennin.hatvdash.model

data class DashboardPanel(
    val title: String,
    val urlPath: String,
    val icon: String? = "mdi:view-dashboard"
)