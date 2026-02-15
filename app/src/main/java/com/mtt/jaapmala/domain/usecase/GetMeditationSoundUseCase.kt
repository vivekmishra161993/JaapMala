package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeditationSoundUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> = repo.meditationSoundEnabled
}
