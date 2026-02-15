package com.mtt.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SoundModeSettingsItem(selectedMode: SoundMode, onModeSelected: (SoundMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
    {

        SoundModeOption(
            title = "Off",
            subtitle = "No sound during chanting",
            selected = selectedMode == SoundMode.OFF,
            onClick = { onModeSelected(SoundMode.OFF) }
        )
        SoundModeOption(
            title = "Mala Completion",
            subtitle = "Play bell after mala completes",
            selected = selectedMode == SoundMode.MALA_COMPLETION,
            onClick = { onModeSelected(SoundMode.MALA_COMPLETION) }
        )
        SoundModeOption(
            title = "Every Jaap",
            subtitle = "Play sound on every jaap",
            selected = selectedMode == SoundMode.EVERY_COUNT,
            onClick = { onModeSelected(SoundMode.EVERY_COUNT) }
        )

    }
}