package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import javax.inject.Inject

class DeactivateDailyGoalUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {

    suspend operator fun invoke(goalId: Int) {
        repository.deactivateGoal(goalId)
    }
}