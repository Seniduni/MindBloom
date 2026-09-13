package com.mindbloom.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic colours that swap between light and dark mode. Brand accents
 * (purple, green, orange...) stay fixed so the app keeps its identity.
 */
@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val field: Color,
    val border: Color,
    val track: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val isDark: Boolean
)

private val LightAppColors = AppColors(
    background = LightBackground,
    surface = LightSurface,
    field = LightField,
    border = LightBorder,
    track = LightTrack,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    isDark = false
)

private val DarkAppColors = AppColors(
    background = DarkBackground,
    surface = DarkSurface,
    field = DarkField,
    border = DarkBorder,
    track = DarkTrack,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    isDark = true
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/** Shorthand accessor: `MB.colors.textPrimary`. */
object MB {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current
}

private val LightScheme = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Teal,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    error = Red
)

private val DarkScheme = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Color.White,
    secondary = Teal,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    error = Red
)

@Composable
fun MindBloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = MindBloomTypography,
            content = content
        )
    }
}
