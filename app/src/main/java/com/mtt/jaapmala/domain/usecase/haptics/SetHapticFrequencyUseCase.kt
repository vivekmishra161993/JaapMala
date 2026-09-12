package com.mtt.jaapmala.domain.usecase.haptics

import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetHapticFrequencyUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(freq: Int) {
        repo.setHapticFeedbackFrequency(freq)
    }
}
