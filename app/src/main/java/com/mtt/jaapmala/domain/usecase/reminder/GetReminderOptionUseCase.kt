package com.mtt.jaapmala.domain.usecase.reminder

import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderOptionUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<ReminderOption> = repo.reminderOption
}
