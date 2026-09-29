package com.mtt.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

// Midnight Blue
val MidnightBlueColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5),       // Vibrant blue for highlights
    onPrimary = Color.White,
    secondary = Color(0xFF90CAF9),     // Soft sky blue accent
    onSecondary = Color.Black,
    background = Color(0xFF0A0E27),    // Deep navy background
    onBackground = Color(0xFFE0EAF8),  // Brighter text for readability
    surface = Color(0xFF121737),       // Slightly lighter surface
    onSurface = Color(0xFFE0EAF8),
    error = Color(0xFFE57373)
)

// Sandalwood (Warm earthy tones)
val ForestEmeraldColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),       // Deep forest green
    onPrimary = Color.White,
    secondary = Color(0xFF81C784),     // Fresh green accent
    background = Color(0xFFF1F8E9),    // Soft natural base
    surface = Color(0xFFE8F5E9),
    onBackground = Color(0xFF1B5E20),  // Rich earthy text
    onSurface = Color(0xFF1B5E20),
    error = Color(0xFFD32F2F)
)


// Lotus Pink (Soft meditation-inspired theme)
val LotusPinkColorScheme = lightColorScheme(
    primary = Color(0xFFEAA4B3), // soft pink
    secondary = Color(0xFFD7BDE2), // lilac tint
    background = Color(0xFFFFF7F8),
    surface = Color(0xFFFFEEF1),
    onBackground = Color(0xFF2E2E2E),
    onSurface = Color(0xFF2E2E2E)
)

@Composable
fun JaapMalaTheme(
    themeOption: ThemeOption = ThemeOption.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeOption) {
        ThemeOption.SYSTEM -> {
            val isDark = isSystemInDarkTheme()
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (isDark) DarkColorScheme else LightColorScheme
            }
        }

        ThemeOption.LIGHT -> LightColorScheme
        ThemeOption.DARK -> DarkColorScheme
        ThemeOption.MIDNIGHT_BLUE -> MidnightBlueColorScheme
        ThemeOption.FOREST_EMERALD -> ForestEmeraldColorScheme
        ThemeOption.LOTUS_PINK -> LotusPinkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

