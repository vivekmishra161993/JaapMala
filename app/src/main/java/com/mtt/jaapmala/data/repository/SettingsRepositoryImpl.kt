package com.mtt.jaapmala.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mtt.jaapmala.data.repository.SettingsRepositoryImpl.PreferencesKeys.KEY_REMINDER_ENABLED
import com.mtt.jaapmala.data.repository.SettingsRepositoryImpl.PreferencesKeys.KEY_REMINDER_TIME
import com.mtt.jaapmala.data.repository.SettingsRepositoryImpl.PreferencesKeys.SOUND_MODE_KEY
import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.presentation.ui.screens.settings.SoundMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("user_preferences")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val REMINDER_OPTION = stringPreferencesKey("reminder_option")
        val MEDITATION_SOUND = booleanPreferencesKey("meditation_sound")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val HAPTIC_FEEDBACK_FREQUENCY = intPreferencesKey("haptic_feedback_frequency")
        val THEME_OPTION = stringPreferencesKey("theme_option")
        val KEY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val KEY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time")
        val SOUND_MODE_KEY = stringPreferencesKey("sound_mode")

    }

    override val themeOption: Flow<ThemeOption> = context.dataStore.data
        .map { prefs ->
            val value = prefs[PreferencesKeys.THEME_OPTION]
            ThemeOption.entries.find { it.name == value } ?: ThemeOption.SYSTEM
        }
    override val isDailyReminderEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_REMINDER_ENABLED] ?: false }
    override val reminderTime: Flow<String> =
        context.dataStore.data.map { prefs -> prefs[KEY_REMINDER_TIME] ?: "20:00" }

    override val reminderOption: Flow<ReminderOption> = context.dataStore.data
        .map { prefs ->
            val value = prefs[PreferencesKeys.REMINDER_OPTION]
            ReminderOption.entries.find { it.name == value } ?: ReminderOption.OFF
        }

    override val meditationSoundEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.MEDITATION_SOUND] ?: true
        }

    override val hapticFeedbackEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.HAPTIC_FEEDBACK] ?: true
        }

    override val hapticFeedbackFrequency: Flow<Int> = context.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.HAPTIC_FEEDBACK_FREQUENCY] ?: 1
        }
    override val soundMode: Flow<SoundMode> =
        context.dataStore.data.map { prefs ->
            SoundMode.valueOf(
                prefs[SOUND_MODE_KEY] ?: SoundMode.MALA_COMPLETION.name
            )

        }

    override suspend fun setReminderOption(option: ReminderOption) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.REMINDER_OPTION] = option.name
        }
    }

    override suspend fun setMeditationSound(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.MEDITATION_SOUND] = enabled
        }
    }

    override suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    override suspend fun setHapticFeedbackFrequency(frequency: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.HAPTIC_FEEDBACK_FREQUENCY] = frequency
        }
    }

    override suspend fun setThemeOption(option: ThemeOption) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_OPTION] = option.name
        }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REMINDER_ENABLED] = enabled }
    }

    override suspend fun setReminderTime(time: String) {
        context.dataStore.edit { it[KEY_REMINDER_TIME] = time }
    }

    override suspend fun setSoundMode(mode: SoundMode) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_MODE_KEY] = mode.name
        }
    }

}
