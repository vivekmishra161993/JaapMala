package com.mtt.jaapmala.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mtt.jaapmala.domain.model.ReminderOption
import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("user_preferences")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
   @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val REMINDER_OPTION = stringPreferencesKey("reminder_option")
        val MEDITATION_SOUND = booleanPreferencesKey("meditation_sound")
        val THEME_OPTION = stringPreferencesKey("theme_option")

    }

    override val themeOption: Flow<ThemeOption> = context.dataStore.data
        .map { prefs ->
            val value = prefs[PreferencesKeys.THEME_OPTION]
            ThemeOption.entries.find { it.name == value } ?: ThemeOption.SYSTEM
        }
    override val reminderOption: Flow<ReminderOption> = context.dataStore.data
        .map { prefs ->
            val value = prefs[PreferencesKeys.REMINDER_OPTION]
            ReminderOption.entries.find { it.name == value } ?: ReminderOption.OFF
        }

    override val meditationSoundEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.MEDITATION_SOUND] ?: true
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
    override suspend fun setThemeOption(option: ThemeOption) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_OPTION] = option.name
        }
    }
}
