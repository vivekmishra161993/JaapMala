package com.mtt.jaapmala.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val REMINDER_OPTION = stringPreferencesKey("reminder_option")
    val MEDITATION_SOUND = booleanPreferencesKey("meditation_sound")
}