package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.data.mapper.toDomain
import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JaapHistoryRepositoryImpl(
    private val dao: JaapHistoryDao
) : JaapHistoryRepository {

    override suspend fun insertOrUpdateHistory(
        jaapId: Int,
        date: String,
        count: Int,
        malaCount: Int
    ) {
        val isoDate = DateUtils.toIsoDate(date)
        val legacyDate = try {
            val parsed = LocalDate.parse(isoDate)
            parsed.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        } catch (_: Exception) {
            null
        }

        if (legacyDate != null && legacyDate != isoDate) {
            dao.deleteHistoryForDate(jaapId, legacyDate)
        }

        dao.insertOrUpdate(
            JaapHistoryEntity(
                jaapId = jaapId,
                date = isoDate,
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
        return dao.getHistoryForDate(jaapId, DateUtils.toIsoDate(date)).map { it?.toDomain() }
    }

    override fun getHistoryBetweenDates(
        jaapId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<JaapHistory>> {
        return dao
            .getHistoryBetweenDates(
                jaapId = jaapId,
                startDate = DateUtils.toIsoDate(startDate),
                endDate = DateUtils.toIsoDate(endDate)
            )
            .map { historyList ->
                historyList.map { it.toDomain() }
            }
    }
}
