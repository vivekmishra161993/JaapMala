package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.JaapCountCalculator
import com.mtt.jaapmala.domain.repository.JaapRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateJaapManuallyUseCase @Inject constructor(
    private val repository: JaapRepository,
    private val jaapCountCalculator: JaapCountCalculator
) {
    suspend operator fun invoke(jaapId: Int, addedCount: Int) {
        val currentJaap = repository.getMantra(jaapId).firstOrNull() ?: return
        val updatedJaap = jaapCountCalculator.calculateNewCounts(currentJaap, addedCount)
        repository.updateJaap(updatedJaap)
    }
}