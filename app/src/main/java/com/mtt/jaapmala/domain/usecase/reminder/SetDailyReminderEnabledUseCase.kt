package com.mtt.jaapmala.domain.usecase.reminder

import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetDailyReminderEnabledUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repo.setDailyReminderEnabled(enabled)
    }
}