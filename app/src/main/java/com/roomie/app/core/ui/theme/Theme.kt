package com.roomie.app.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary  = Grey50,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue700,
    secondary = Teal500,
    onSecondary = Grey50,
    background = Grey50,
    onBackground = Grey900,
    surface = Grey100,
    onSurface = Grey900,
    surfaceVariant = Grey200,
    onSurfaceVariant = Grey600,
    error = RedError,
    onError = Grey50,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue100,
    onPrimary  = Blue700,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue100,
    secondary = Teal500,
    onSecondary = Grey900,
    background = Grey900,
    onBackground = Grey50,
    surface = Color(0xFF1E1E1E),
    onSurface = Grey50,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Grey200,
    error = RedError,
    onError = Grey50,
)

@Composable
fun RoomieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}