package com.roomie.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary  = SurfaceWhite,
    primaryContainer = TealPrimary,
    onPrimaryContainer = TealDark,
    secondary = NavySecondary,
    onSecondary = SurfaceWhite,
    background = BackgroundLight,
    onBackground = NavyPrimary,
    surface = SurfaceWhite,
    onSurface = NavyPrimary,
    surfaceVariant = InputBackground,
    onSurfaceVariant = NavySecondary,
    outline = InputBorder,
    error = DestructiveRed,
    onError = SurfaceWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = BackgroundDark,
    primaryContainer = TealDark,
    onPrimaryContainer = TealLight,
    secondary = NavySecondaryDark,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = NavyPrimaryDark,
    surface = SurfaceDark,
    onSurface = NavyPrimaryDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = NavySecondaryDark,
    outline = NavySecondary,
    error = DestructiveRed,
    onError = SurfaceWhite,
)

@Composable
fun RoomieTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RoomieTypography,
        shapes = RoomieShapes,
        content = content
    )
}