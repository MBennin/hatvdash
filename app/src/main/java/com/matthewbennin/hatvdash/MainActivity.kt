package com.matthewbennin.hatvdash

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.matthewbennin.hatvdash.network.HaWebSocketManager
import com.matthewbennin.hatvdash.network.HomeAssistantConfig
import com.matthewbennin.hatvdash.ui.theme.HATVDashTheme
import com.matthewbennin.hatvdash.ui.DashboardEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HaWebSocketManager.connect(
            baseUrl = HomeAssistantConfig.BASE_URL,
            token = HomeAssistantConfig.TOKEN
        )

        setContent {
            HATVDashTheme(isInDarkTheme = true) {
                DashboardEntryPoint()
            }
        }
    }
}
