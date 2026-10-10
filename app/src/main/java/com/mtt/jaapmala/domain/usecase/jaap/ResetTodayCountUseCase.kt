package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.domain.repository.JaapRepository
import javax.inject.Inject

class ResetTodayCountsUseCase @Inject constructor(
    private val repo: JaapRepository
) {
    suspend operator fun invoke() {
        repo.resetTodayCountsIfNeeded()
    }
}