package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import com.mtt.jaapmala.util.DateUtils.getTodayDate
import javax.inject.Inject

class CreateDailyGoalUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {

    suspend operator fun invoke(
        jaapId: Int,
        targetMalas: Int
    ) {
        require(targetMalas > 0) {
            "Target malas must be greater than zero"
        }

        repository.createDailyGoal(
            jaapId = jaapId,
            targetMalas = targetMalas,
            effectiveFrom = getTodayDate()
        )
    }
}