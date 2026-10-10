package com.mtt.jaapmala.domain.usecase.jaap

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
}