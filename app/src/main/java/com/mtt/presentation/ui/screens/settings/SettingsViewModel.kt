package com.mtt.presentation.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.jaapmala.domain.usecase.CancelReminderUseCase
import com.mtt.jaapmala.domain.usecase.ScheduleReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val repo: SettingsRepository
) : ViewModel() {

    val themeOption = repo.themeOption.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ThemeOption.SYSTEM
    )

    val meditationSoundEnabled = repo.meditationSoundEnabled.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        true
    )
    val isDailyReminderEnabled = repo.isDailyReminderEnabled.stateIn(
        viewModelScope, SharingStarted.Lazily, false
    )

    val reminderTime = repo.reminderTime.stateIn(
        viewModelScope, SharingStarted.Lazily, "20:00"
    )

    fun updateReminder(option: ReminderOption) {
        viewModelScope.launch { repo.setReminderOption(option) }
    }

    fun toggleMeditationSound(enabled: Boolean) {
        viewModelScope.launch { repo.setMeditationSound(enabled) }
    }

    fun updateTheme(option: ThemeOption) {
        viewModelScope.launch { repo.setThemeOption(option) }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            repo.setDailyReminderEnabled(enabled)
            if (enabled) {
                scheduleReminderUseCase(reminderTime.value)
            } else {
                cancelReminderUseCase()
            }
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            repo.setReminderTime(time)
            if (isDailyReminderEnabled.value) {
                scheduleReminderUseCase(time)
            }
        }
    }
}
