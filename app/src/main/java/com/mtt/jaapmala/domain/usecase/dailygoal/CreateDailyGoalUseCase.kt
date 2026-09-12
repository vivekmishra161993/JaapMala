package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import javax.inject.Inject

class CreateDailyGoalUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {

    suspend operator fun invoke(
        jaapId: Int,
        targetMalas: Int,
        startDate: String,
        endDate: String?
    ) {
        require(targetMalas > 0) {
            "Target malas must be greater than zero"
        }

        require(endDate == null || endDate >= startDate) {
            "End date cannot be before start date"
        }

        repository
            .getActiveGoalForJaapOnce(jaapId)
            ?.let { existingGoal ->
                repository.deactivateGoal(existingGoal.id)
            }

        repository.insert(
            DailyGoal(
                id = 0,
                jaapId = jaapId,
                targetMalas = targetMalas,
                startDate = startDate,
                endDate = endDate,
                isActive = true
            )
        )
    }
}