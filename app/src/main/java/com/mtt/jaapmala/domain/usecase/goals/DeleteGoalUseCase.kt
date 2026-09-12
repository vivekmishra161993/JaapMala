package com.mtt.jaapmala.domain.usecase.goals

import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(private val repo: GoalRepository) {
    suspend operator fun invoke(goal: GoalEntity) = repo.deleteGoal(goal)
}
