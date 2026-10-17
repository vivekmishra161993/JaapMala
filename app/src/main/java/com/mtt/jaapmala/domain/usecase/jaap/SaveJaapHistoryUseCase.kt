package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository

class SaveJaapHistoryUseCase(
    private val repository: JaapHistoryRepository
) {
    suspend operator fun invoke(
        jaapId: Int,
        date: String,
        count: Int,
        malaCount: Int
    ) {
        repository.insertOrUpdateHistory(
            jaapId = jaapId,
            date = date,
            count = count,
            malaCount = malaCount
        )
    }

    suspend operator fun invoke(entity: JaapEntity) {
        repository.insertOrUpdateHistory(
            jaapId = entity.id,
            date = entity.date,
            count = entity.todayCount,
            malaCount = entity.todayMalaCount
        )
    }
}
