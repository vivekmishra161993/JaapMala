package com.mtt.jaapmala.data.local.db

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri

object BackupPrefs {
    private const val PREF_NAME = "backup_prefs"
    private const val KEY_BACKUP_URI = "backup_uri"
    private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"

    fun saveBackupUri(context: Context, uri: Uri) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit() { putString(KEY_BACKUP_URI, uri.toString()) }
    }

    fun getBackupUri(context: Context): Uri? {
        val uriString = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_BACKUP_URI, null)
        return uriString?.toUri()
    }
    fun setOnboardingShown(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ONBOARDING_SHOWN, true).apply()
    }

    fun isOnboardingShown(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_SHOWN, false)
    }
}
