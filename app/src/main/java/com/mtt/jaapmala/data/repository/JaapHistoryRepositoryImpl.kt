package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow

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

    override fun getHistoryForJaap(jaapId: Int): Flow<List<JaapHistoryEntity>> {
        return dao.getHistoryForJaap(jaapId)
    }
}
