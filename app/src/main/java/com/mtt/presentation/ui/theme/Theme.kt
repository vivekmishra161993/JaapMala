package com.mtt.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mtt.jaapmala.domain.model.ThemeOption


val LightColorScheme = lightColorScheme(
    primary = Copper,
    onPrimary = Color.White,
    secondary = Gold,
    onSecondary = Charcoal,
    background = Cream,
    onBackground = Charcoal,
    surface = SageGreenLight,
    onSurface = Charcoal,
    error = ErrorRed
)

val DarkColorScheme = darkColorScheme(
    primary = Copper,                 // FAB, TopAppBar, Buttons
    onPrimary = Color.White,

    secondary = Color(0xFFA5D6A7),    // Soft Sage as accent
    onSecondary = Color(0xFF1C1C1C),

    background = Color(0xFF121212),   // App background
    onBackground = Color(0xFFF5F5F5),

    surface = Color(0xFF1E1E1E),      // Card surface
    onSurface = Color(0xFFE0E0E0),

    error = ErrorRed
)

@Composable
fun JaapMalaTheme(
    themeOption: ThemeOption = ThemeOption.SYSTEM,
    content: @Composable () -> Unit

) {
    val darkTheme = when (themeOption) {
        ThemeOption.LIGHT -> false
        ThemeOption.DARK -> true
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}