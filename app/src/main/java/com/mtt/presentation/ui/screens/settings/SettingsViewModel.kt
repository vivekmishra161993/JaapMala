package com.mtt.presentation.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.usecase.CancelReminderUseCase
import com.mtt.jaapmala.domain.usecase.GetDailyReminderEnabledUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFeedbackUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFrequencyUseCase
import com.mtt.jaapmala.domain.usecase.GetMeditationSoundUseCase
import com.mtt.jaapmala.domain.usecase.GetReminderTimeUseCase
import com.mtt.jaapmala.domain.usecase.GetSoundModeUseCase
import com.mtt.jaapmala.domain.usecase.GetThemeOptionUseCase
import com.mtt.jaapmala.domain.usecase.ScheduleReminderUseCase
import com.mtt.jaapmala.domain.usecase.SetDailyReminderEnabledUseCase
import com.mtt.jaapmala.domain.usecase.SetHapticFeedbackUseCase
import com.mtt.jaapmala.domain.usecase.SetHapticFrequencyUseCase
import com.mtt.jaapmala.domain.usecase.SetMeditationSoundUseCase
import com.mtt.jaapmala.domain.usecase.SetReminderOptionUseCase
import com.mtt.jaapmala.domain.usecase.SetReminderTimeUseCase
import com.mtt.jaapmala.domain.usecase.SetSoundModeUseCase
import com.mtt.jaapmala.domain.usecase.SetThemeOptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val cancelReminderUseCase: CancelReminderUseCase,
    private val setThemeOptionUseCase: SetThemeOptionUseCase,
    getThemeOptionUseCase: GetThemeOptionUseCase,
    private val setMeditationSoundUseCase: SetMeditationSoundUseCase,
    getMeditationSoundUseCase: GetMeditationSoundUseCase,
    private val setHapticFeedbackUseCase: SetHapticFeedbackUseCase,
    getHapticFeedbackUseCase: GetHapticFeedbackUseCase,
    private val setHapticFrequencyUseCase: SetHapticFrequencyUseCase,
    getHapticFrequencyUseCase: GetHapticFrequencyUseCase,
    private val setDailyReminderEnabledUseCase: SetDailyReminderEnabledUseCase,
    getDailyReminderEnabledUseCase: GetDailyReminderEnabledUseCase,
    private val setReminderTimeUseCase: SetReminderTimeUseCase,
    getReminderTimeUseCase: GetReminderTimeUseCase,
    private val setReminderOptionUseCase: SetReminderOptionUseCase,
    getSoundModeUseCase: GetSoundModeUseCase,
    private val setSoundModeUseCase: SetSoundModeUseCase
    ) : ViewModel() {
    val themeOption = getThemeOptionUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ThemeOption.SYSTEM)

    val meditationSoundEnabled = getMeditationSoundUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    val hapticFeedbackEnabled = getHapticFeedbackUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        true
    )

    val hapticFeedbackFrequency = getHapticFrequencyUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        1
    )

    val isDailyReminderEnabled = getDailyReminderEnabledUseCase().stateIn(
        viewModelScope, SharingStarted.Lazily, false
    )

    val reminderTime = getReminderTimeUseCase().stateIn(
        viewModelScope, SharingStarted.Lazily, "20:00"
    )
    val soundMode = getSoundModeUseCase().stateIn(
        viewModelScope, SharingStarted.Lazily, SoundMode.MALA_COMPLETION
    )


    fun updateReminder(option: ReminderOption) {
        viewModelScope.launch { setReminderOptionUseCase(option) }
    }

    fun toggleMeditationSound(enabled: Boolean) {
        viewModelScope.launch { setMeditationSoundUseCase(enabled) }
    }

    fun toggleHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { setHapticFeedbackUseCase(enabled) }
    }

    fun setHapticFeedbackFrequency(frequency: Int) {
        viewModelScope.launch { setHapticFrequencyUseCase(frequency) }
    }

    fun updateTheme(option: ThemeOption) {
        viewModelScope.launch { setThemeOptionUseCase(option) }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            setDailyReminderEnabledUseCase(enabled)
            if (enabled) {
                scheduleReminderUseCase(reminderTime.value)
            } else {
                cancelReminderUseCase()
            }
        }
    }

    fun updateReminderTime(time: String) {
        viewModelScope.launch {
            setReminderTimeUseCase(time)
            if (isDailyReminderEnabled.value) {
                scheduleReminderUseCase(time)
            }
        }
    }
    fun updateSoundMode(mode: SoundMode) {
        viewModelScope.launch {
            setSoundModeUseCase(mode)
        }
    }

}
