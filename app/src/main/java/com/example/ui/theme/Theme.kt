package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SomnoDarkColorScheme = darkColorScheme(
    primary = CelestialIndigo,
    onPrimary = Color.White,
    primaryContainer = MidnightCardElevated,
    onPrimaryContainer = TextPrimary,
    secondary = CelestialCyan,
    onSecondary = Color.Black,
    secondaryContainer = MidnightCard,
    onSecondaryContainer = TextPrimary,
    tertiary = CelestialPink,
    onTertiary = Color.White,
    background = MidnightDark,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassSurface
)

@Composable
fun SomnoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SomnoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
