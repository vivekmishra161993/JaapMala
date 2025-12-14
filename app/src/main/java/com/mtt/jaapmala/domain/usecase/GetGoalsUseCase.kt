package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.GoalRepository
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(private val repo: GoalRepository) {
    operator fun invoke() = repo.getAllGoals()
}