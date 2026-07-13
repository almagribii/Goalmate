package com.almagribii.goalmate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = SkyBlueSecondary,
    tertiary = OrangeAccent,
    background = AppBackground,
    surface = AppSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    secondary = SkyBlueSecondary,
    tertiary = OrangeAccent,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun GoalmateTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}