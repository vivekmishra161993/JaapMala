package com.mtt.jaapmala.domain.usecase.sound

import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.presentation.ui.screens.settings.SoundMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSoundModeUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<SoundMode> = repo.soundMode
}
