package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetReminderOptionUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(option: ReminderOption) {
        repo.setReminderOption(option)
    }
}