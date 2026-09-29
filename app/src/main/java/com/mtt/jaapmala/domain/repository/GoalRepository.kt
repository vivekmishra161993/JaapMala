package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.presentation.ui.screens.goals.GoalUiModel
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun addGoal(goal: GoalEntity)

    suspend fun updateGoal(goal: List<GoalEntity>)

    suspend fun deleteGoal(goal: GoalEntity)

    fun getAllGoals(): Flow<List<GoalUiModel>>

    suspend fun getGoalById(goalId: Int): GoalEntity?

    suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity>

    suspend fun getActiveGoals(): List<GoalEntity>
    suspend fun findActiveGoalByJaapId(jaapId: Int): List<GoalEntity>
}