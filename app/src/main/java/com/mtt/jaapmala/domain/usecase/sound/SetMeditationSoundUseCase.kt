package com.mtt.jaapmala.domain.usecase.sound

import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetMeditationSoundUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repo.setMeditationSound(enabled)
    }
}
