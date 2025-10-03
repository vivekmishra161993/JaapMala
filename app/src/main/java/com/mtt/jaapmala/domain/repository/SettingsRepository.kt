package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.model.ThemeOption
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val reminderOption: Flow<ReminderOption>
    val meditationSoundEnabled: Flow<Boolean>
    val themeOption: Flow<ThemeOption>
    suspend fun setReminderOption(option: ReminderOption)
    suspend fun setMeditationSound(enabled: Boolean)
    suspend fun setThemeOption(option: ThemeOption)
}
