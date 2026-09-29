package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.presentation.ui.screens.settings.SoundMode
import javax.inject.Inject

class SetSoundModeUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(mode: SoundMode) {
        repo.setSoundMode(mode)
    }
}