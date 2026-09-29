package com.mtt.jaapmala.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.mtt.jaapmala.domain.repository.ChangelogRepository
import com.mtt.presentation.ui.screens.whats_new.ChangelogPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChangeLogRepoImpl(
    @ApplicationContext private val context: Context
) : ChangelogRepository {
    private val dataStore = context.dataStore

    override val lastShownVersion: Flow<Int> =
        dataStore.data.map {
            it[ChangelogPrefs.LAST_SHOWN_VERSION] ?: 7
        }

    override suspend fun markVersionShown(versionCode: Int) {
        dataStore.edit {
            it[ChangelogPrefs.LAST_SHOWN_VERSION] = versionCode
        }
    }
}
