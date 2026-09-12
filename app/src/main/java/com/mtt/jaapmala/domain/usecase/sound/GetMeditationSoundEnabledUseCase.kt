package com.mtt.jaapmala.domain.usecase.sound

import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeditationSoundEnabledUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.meditationSoundEnabled
}
