package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveDailyGoalsUseCase @Inject constructor(
    private val repository: DailyGoalRepository
) {

    operator fun invoke(): Flow<List<DailyGoal>> {
        return repository.getActiveGoals()
    }
}