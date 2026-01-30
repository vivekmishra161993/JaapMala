package com.mtt.jaapmala.domain.repository

import kotlinx.coroutines.flow.Flow

interface ChangelogRepository {
    val lastShownVersion: Flow<Int>
    suspend fun markVersionShown(versionCode: Int)
}