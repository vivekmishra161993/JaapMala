package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetJaapHistoryUseCase(
    private val repository: JaapHistoryRepository
) {
    operator fun invoke(jaapId: Int): Flow<List<JaapHistoryEntity>> {
        return repository.getHistoryForJaap(jaapId)
    }
}