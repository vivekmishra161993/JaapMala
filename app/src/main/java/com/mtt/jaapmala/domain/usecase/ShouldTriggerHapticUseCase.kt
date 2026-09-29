package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ShouldTriggerHapticUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    fun shouldTrigger(
        newCount: Int
    ): Flow<Boolean> {
        return combine(
            settingsRepository.hapticFeedbackEnabled,
            settingsRepository.hapticFeedbackFrequency
        ) { enabled, frequency ->
            enabled && frequency > 0 && newCount % frequency == 0
        }
    }
}

