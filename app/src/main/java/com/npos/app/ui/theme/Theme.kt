package com.npos.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PosPrimary = Color(0xFF0D6EFD)
val PosPrimaryDark = Color(0xFF0A58CA)
val PosSecondary = Color(0xFF495057)
val PosSuccess = Color(0xFF198754)
val PosWarning = Color(0xFFFD7E14)
val PosBackground = Color(0xFFF8F9FA)
val PosSurface = Color(0xFFFFFFFF)
val PosOnPrimary = Color(0xFFFFFFFF)
val PosOnBackground = Color(0xFF212529)

private val LightColorScheme = lightColorScheme(
    primary = PosPrimary,
    onPrimary = PosOnPrimary,
    primaryContainer = Color(0xFFD0E1FD),
    onPrimaryContainer = Color(0xFF001D4A),
    secondary = PosSecondary,
    background = PosBackground,
    surface = PosSurface,
    onBackground = PosOnBackground,
    onSurface = PosOnBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF002F6C),
    primaryContainer = Color(0xFF0D6EFD),
    onPrimaryContainer = Color(0xFFD0E1FD),
    secondary = Color(0xFFADB5BD),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE2E2E2),
    onSurface = Color(0xFFE2E2E2)
)

@Composable
fun NPOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
