package com.npos.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.npos.app.data.AppLanguage
import com.npos.app.data.AppThemeMode

val PosPrimary = Color(0xFF0D6EFD)
val PosPrimaryDark = Color(0xFF0A58CA)
val PosSecondary = Color(0xFF495057)
val PosSuccess = Color(0xFF198754)
val PosWarning = Color(0xFFFD7E14)
val PosBackground = Color(0xFFF4F6F9)
val PosSurface = Color(0xFFFFFFFF)
val PosOnPrimary = Color(0xFFFFFFFF)
val PosOnBackground = Color(0xFF212529)

private val LightColorScheme = lightColorScheme(
    primary = PosPrimary,
    onPrimary = PosOnPrimary,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = PosSecondary,
    onSecondary = Color.White,
    background = PosBackground,
    onBackground = PosOnBackground,
    surface = PosSurface,
    onSurface = PosOnBackground,
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF495057),
    outline = Color(0xFFDEE2E6)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF002A54),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF212529),
    background = Color(0xFF121417),
    onBackground = Color(0xFFE9ECEF),
    surface = Color(0xFF1E2126),
    onSurface = Color(0xFFE9ECEF),
    surfaceVariant = Color(0xFF2B3038),
    onSurfaceVariant = Color(0xFFADB5BD),
    outline = Color(0xFF3E444E)
)

@Composable
fun NPOSTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    language: AppLanguage = AppLanguage.MM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val strings = getAppStrings(language)

    CompositionLocalProvider(
        LocalStrings provides strings,
        LocalAppLanguage provides language,
        LocalThemeMode provides themeMode
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
