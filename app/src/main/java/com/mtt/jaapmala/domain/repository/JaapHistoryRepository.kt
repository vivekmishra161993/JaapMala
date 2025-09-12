package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import kotlinx.coroutines.flow.Flow

interface JaapHistoryRepository {

    suspend fun insertOrUpdateHistory(
        jaapId: Int,
        date: String,
        count: Int,
        malaCount: Int
    )

    fun getHistoryForJaap(jaapId: Int): Flow<List<JaapHistoryEntity>>
}