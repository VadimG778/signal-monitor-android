package com.vadimg778.signalmonitor.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SignalMonitorColors.lightPrimary,
    secondary = SignalMonitorColors.lightSecondary,
    tertiary = SignalMonitorColors.lightTertiary,
)

private val DarkColorScheme = darkColorScheme(
    primary = SignalMonitorColors.darkPrimary,
    secondary = SignalMonitorColors.darkSecondary,
    tertiary = SignalMonitorColors.darkTertiary,
)

@Composable
fun SignalMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
