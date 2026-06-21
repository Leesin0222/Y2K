package com.yongjincomapny.y2k.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val Y2KLightColorScheme = lightColorScheme(
    primary = Y2KAccent,
    onPrimary = Color.White,
    primaryContainer = Y2KAccentDim,
    onPrimaryContainer = Y2KAccent,
    secondary = Y2KInk,
    onSecondary = Y2KCream,
    secondaryContainer = Y2KSurfaceContainer,
    onSecondaryContainer = Y2KInk,
    tertiary = Y2KWarning,
    onTertiary = Color.White,
    background = Y2KCream,
    onBackground = Y2KInk,
    surface = Y2KSurface,
    onSurface = Y2KInk,
    surfaceVariant = Y2KSurfaceRaised,
    onSurfaceVariant = Y2KInkMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Y2KSurfaceRaised,
    surfaceContainer = Y2KSurfaceContainer,
    surfaceContainerHigh = Y2KBorder,
    outline = Y2KBorder,
    outlineVariant = Y2KBorder.copy(alpha = 0.5f),
    error = Y2KError,
    onError = Color.White,
    errorContainer = Y2KErrorContainer,
    onErrorContainer = Y2KOnErrorContainer,
    inverseSurface = Y2KInk,
    inverseOnSurface = Y2KCream,
    inversePrimary = Color(0xFF93B4F5),
    scrim = Color.Black.copy(alpha = 0.32f),
)

private val Y2KDarkColorScheme = darkColorScheme(
    primary = Y2KDarkAccent,
    onPrimary = Color.White,
    primaryContainer = Y2KDarkAccentDim,
    onPrimaryContainer = Y2KDarkAccent,
    secondary = Y2KDarkFg,
    onSecondary = Y2KDarkBg,
    secondaryContainer = Y2KDarkSurfaceContainer,
    onSecondaryContainer = Y2KDarkFg,
    tertiary = Y2KWarning,
    onTertiary = Color.White,
    background = Y2KDarkBg,
    onBackground = Y2KDarkFg,
    surface = Y2KDarkSurface,
    onSurface = Y2KDarkFg,
    surfaceVariant = Y2KDarkSurfaceRaised,
    onSurfaceVariant = Y2KDarkFgMuted,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Y2KDarkSurface,
    surfaceContainer = Y2KDarkSurfaceContainer,
    surfaceContainerHigh = Y2KDarkSurfaceRaised,
    outline = Y2KDarkBorder,
    outlineVariant = Y2KDarkBorder.copy(alpha = 0.5f),
    error = Y2KError,
    onError = Color.White,
    errorContainer = Y2KDarkErrorContainer,
    onErrorContainer = Y2KDarkOnErrorContainer,
    inverseSurface = Y2KDarkFg,
    inverseOnSurface = Y2KDarkBg,
    inversePrimary = Y2KAccent,
    scrim = Color.Black.copy(alpha = 0.5f),
)

@Composable
fun Y2KTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) Y2KDarkColorScheme else Y2KLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Y2KTypography,
        content = content,
    )
}
