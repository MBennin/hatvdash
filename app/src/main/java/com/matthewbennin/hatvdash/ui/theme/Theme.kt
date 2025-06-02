package com.matthewbennin.hatvdash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
//import androidx.compose.material3.ExperimentalTvMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val HASSLightPrimary = Color(0xFF385E8D)     // Icon blue
val HASSLightAccent = Color(0xFFFF9800)      // Orange badge
val HASSLightBackground = Color(0xFFFAFAFA)  // Page background
val HASSLightSurface = Color(0xFFFFFFFF)     // Cards and panels
val HASSLightText = Color(0xFF212121)        // Primary text
val HASSLightBorder = Color(0xFFDDDDDD)      // Outlines

val HASSDarkPrimary = Color(0xFF4180C3)
val HASSDarkAccent = Color(0xFFFFB300)
val HASSDarkBackground = Color(0xFF121212)
val HASSDarkSurface = Color(0xFF1F1F1F)
val HASSDarkOnBackground = Color(0xFFFFFFFF)
val HASSDarkOnSurface = Color(0xFFE0E0E0)
val HASSDarkBorder = Color(0xFF2C2C2C)

val BlueAccent = Color(0xFF03A8F4)     // Primary blue
val OrangeAccent = Color(0xFFFF9800)   // Secondary border

var GreyBox = Color(0xFF1C1C1C)

var OffWhite = Color(0xFFF2F0EF)

val OnDarkText = Color(0xFFFFFFFF)

val OnLightText = Color(0xFF000000)



//@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HATVDashTheme(
    isInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) {
        darkColorScheme(
            primary = HASSDarkPrimary,
            secondary = HASSDarkAccent,
            background = HASSDarkBackground,
            surface = HASSDarkSurface,
            onBackground = HASSDarkOnBackground,
            onSurface = HASSDarkOnSurface,
            onPrimary = Color.White,
            onSecondary = Color.Black
        )
    } else {
        lightColorScheme(
            primary = HASSLightPrimary,
            secondary = HASSLightAccent,
            background = HASSLightBackground,
            surface = HASSLightSurface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = HASSLightText,
            onSurface = HASSLightText
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}