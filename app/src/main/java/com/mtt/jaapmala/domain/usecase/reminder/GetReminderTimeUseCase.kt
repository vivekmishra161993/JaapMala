package com.mtt.jaapmala.domain.usecase.reminder

import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReminderTimeUseCase@Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<String> = repo.reminderTime
}
