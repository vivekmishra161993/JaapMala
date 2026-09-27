package com.mtt.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.R

@Composable
fun SoundModeSettingsItem(selectedMode: SoundMode, onModeSelected: (SoundMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
    {

        SoundModeOption(
            title = stringResource(R.string.off),
            subtitle = stringResource(R.string.no_sound_during_chanting),
            selected = selectedMode == SoundMode.OFF,
            onClick = { onModeSelected(SoundMode.OFF) }
        )
        SoundModeOption(
            title = stringResource(R.string.mala_completion),
            subtitle = stringResource(R.string.play_bell_after_mala_completes),
            selected = selectedMode == SoundMode.MALA_COMPLETION,
            onClick = { onModeSelected(SoundMode.MALA_COMPLETION) }
        )
        SoundModeOption(
            title = stringResource(R.string.every_jaap),
            subtitle = stringResource(R.string.play_sound_on_every_jaap),
            selected = selectedMode == SoundMode.EVERY_COUNT,
            onClick = { onModeSelected(SoundMode.EVERY_COUNT) }
        )

    }
}