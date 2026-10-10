package com.mtt.jaapmala.domain.usecase.goals

import com.mtt.jaapmala.data.local.entity.GoalStatus
import com.mtt.jaapmala.domain.repository.GoalRepository
import javax.inject.Inject

class CheckGoalStatusUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke() {
        val today = System.currentTimeMillis()
        val activeGoals = goalRepository.getActiveGoals() // You'll need to create this repo/DAO function

        val goalsToUpdate = activeGoals.filter { goal ->
            goal.endDate?.let { today > it } ?: false
        }.map { goal ->
            val newStatus = if (goal.currentMalas >= goal.targetMalas) {
                GoalStatus.SUCCEEDED
            } else {
                GoalStatus.FAILED
            }
            goal.copy(status = newStatus)
        }

        // 2. If there are any goals to update, send the entire list to the repository.
        if (goalsToUpdate.isNotEmpty()) {
            goalRepository.updateGoal(goalsToUpdate)
        }
    }
}