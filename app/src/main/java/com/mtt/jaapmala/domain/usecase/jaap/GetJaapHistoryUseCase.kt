package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetJaapHistoryUseCase(
    private val repository: JaapHistoryRepository
) {
    operator fun invoke(jaapId: Int): Flow<List<JaapHistory>> {
        return repository.getHistoryForJaap(jaapId)
    }
}