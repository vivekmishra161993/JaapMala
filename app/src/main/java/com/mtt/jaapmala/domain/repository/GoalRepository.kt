package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun addGoal(goal: GoalEntity)

    suspend fun updateGoal(goal: GoalEntity)

    suspend fun deleteGoal(goal: GoalEntity)

    fun getAllGoals(): Flow<List<GoalEntity>>

    suspend fun getGoalById(goalId: Int): GoalEntity?

    suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity>
}