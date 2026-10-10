package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.domain.model.JaapHistory
import kotlinx.coroutines.flow.Flow

interface JaapHistoryRepository {

    suspend fun insertOrUpdateHistory(
        jaapId: Int,
        date: String,
        count: Int,
        malaCount: Int
    )

    fun getHistoryForJaap(jaapId: Int): Flow<List<JaapHistory>>
    fun getHistoryForDate(
        jaapId: Int,
        date: String
    ): Flow<JaapHistory?>

    fun getHistoryBetweenDates(
        jaapId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<JaapHistory>>
}