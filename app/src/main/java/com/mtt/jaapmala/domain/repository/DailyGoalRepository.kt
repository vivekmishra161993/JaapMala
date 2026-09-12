package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.domain.model.DailyGoal
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {

    fun getActiveGoals(): Flow<List<DailyGoal>>

    fun getActiveGoalForJaap(jaapId: Int): Flow<DailyGoal?>

    suspend fun getGoalById(goalId: Int): DailyGoal?

    suspend fun insert(goal: DailyGoal)

    suspend fun update(goal: DailyGoal)

    suspend fun delete(goal: DailyGoal)

    suspend fun deactivateGoal(goalId: Int)
    suspend fun getActiveGoalForJaapOnce(jaapId: Int): DailyGoal?
}