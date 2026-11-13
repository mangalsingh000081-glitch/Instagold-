package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val InstgoldColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = GoldGlow,
    secondary = GoldGlow,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = GoldAccent,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSecondary,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderHighlight,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun InstgoldTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = InstgoldColorScheme,
        typography = Typography,
        content = content
    )
}
