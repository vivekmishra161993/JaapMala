package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.data.mapper.toDomain
import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JaapHistoryRepositoryImpl(
    private val dao: JaapHistoryDao
) : JaapHistoryRepository {

    override suspend fun insertOrUpdateHistory(
        jaapId: Int,
        date: String,
        count: Int,
        malaCount: Int
    ) {
        dao.insertOrUpdate(
            JaapHistoryEntity(
                jaapId = jaapId,
                date = date,
                count = count,
                malaCount = malaCount
            )
        )
    }

    override fun getHistoryForJaap(jaapId: Int): Flow<List<JaapHistory>> {
        return dao.getHistoryForJaap(jaapId)
    }

    override fun getHistoryForDate(
        jaapId: Int,
        date: String
    ): Flow<JaapHistory?> {
        return dao.getHistoryForDate(jaapId,date)
    }
    override fun getHistoryBetweenDates(
        jaapId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<JaapHistory>> {
        return dao
            .getHistoryBetweenDates(
                jaapId = jaapId,
                startDate = startDate,
                endDate = endDate
            )
            .map { historyList ->
                historyList.map { it.toDomain() }
            }
    }
}
