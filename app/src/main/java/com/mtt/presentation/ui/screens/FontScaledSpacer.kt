package com.mtt.presentation.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp

/**
 * A Spacer that adjusts its height based on the user's system font size settings.
 *
 * @param startHeight The height of the spacer at the default font scale (1f).
 * @param endHeight The maximum height of the spacer at a larger font scale (e.g., 2f or more).
 * @param modifier The modifier to be applied to the spacer.
 */
@Composable
fun FontScaledSpacer(
    startHeight: Dp,
    endHeight: Dp,
    modifier: Modifier = Modifier
) {
    // Get the current font scale from the system settings.
    val fontScale = LocalDensity.current.fontScale

    // Calculate a fraction from 0f to 1f based on the font scale.
    // This example assumes a linear interpolation between a font scale of 1f and 2f.
    val fraction = ((fontScale - 1f) / 1f).coerceIn(0f, 1f)

    // Linearly interpolate between the start and end heights using the calculated fraction.
    val spacerHeight = lerp(startHeight, endHeight, fraction)

    // Apply the calculated height to a standard Spacer.
    Spacer(modifier = modifier.height(spacerHeight))
}