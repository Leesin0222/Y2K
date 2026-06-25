package com.yongjincomapny.y2k.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class Y2KColors(
    val bg: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val fg: Color,
    val fgMuted: Color,
    val border: Color,
    val accent: Color,
    val accentDim: Color,
    val onAccent: Color,
    val error: Color,
    val warning: Color,
    val success: Color,
)

val Y2KLightColors = Y2KColors(
    bg = Y2KCream,
    surface = Y2KSurface,
    surfaceRaised = Y2KSurfaceRaised,
    fg = Y2KInk,
    fgMuted = Y2KInkMuted,
    border = Y2KBorder,
    accent = Y2KAccent,
    accentDim = Y2KAccentDim,
    onAccent = Color.White,
    error = Y2KError,
    warning = Y2KWarning,
    success = Y2KSuccess,
)

val Y2KDarkColors = Y2KColors(
    bg = Y2KDarkBg,
    surface = Y2KDarkSurface,
    surfaceRaised = Y2KDarkSurfaceRaised,
    fg = Y2KDarkFg,
    fgMuted = Y2KDarkFgMuted,
    border = Y2KDarkBorder,
    accent = Y2KDarkAccent,
    accentDim = Y2KDarkAccentDim,
    onAccent = Color.White,
    error = Y2KError,
    warning = Y2KWarning,
    success = Y2KSuccess,
)

val LocalY2KColors = staticCompositionLocalOf { Y2KLightColors }
