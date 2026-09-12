package com.mtt.jaapmala.domain.usecase.goals

import com.mtt.jaapmala.data.local.entity.GoalStatus
import com.mtt.jaapmala.domain.repository.GoalRepository
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
            suspend operator fun invoke(jaapId: Int, malaIncrement: Int = 1) {
                val activeGoals = goalRepository.findActiveGoalByJaapId(jaapId)
                if (activeGoals.isEmpty()) return
                val updatedGoals = activeGoals.map { goal ->
                    // Correctly adds to the goal's own progress field
                    val newProgress = goal.currentMalas + malaIncrement
                    val newStatus = if (newProgress >= goal.targetMalas) GoalStatus.SUCCEEDED else goal.status
                    goal.copy(currentMalas = newProgress, status = newStatus)
                }
                // Correctly updates a list of goals
                goalRepository.updateGoal(updatedGoals)
            }
}