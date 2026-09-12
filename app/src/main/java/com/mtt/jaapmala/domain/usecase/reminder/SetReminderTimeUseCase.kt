package com.mtt.jaapmala.domain.usecase.reminder

import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetReminderTimeUseCase@Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(time: String) {
        repo.setReminderTime(time)
    }
}