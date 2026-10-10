package com.mtt.jaapmala.domain.usecase.haptics

import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetHapticFeedbackUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repo.setHapticFeedback(enabled)
    }
}
