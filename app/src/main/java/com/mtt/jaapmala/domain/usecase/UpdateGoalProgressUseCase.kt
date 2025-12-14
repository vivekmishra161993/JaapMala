package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.repository.GoalRepository
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(private val repo: GoalRepository) {
    suspend operator fun invoke(goalEntity: GoalEntity) =
        repo.updateGoal(goalEntity)
}