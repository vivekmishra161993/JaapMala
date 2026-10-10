package com.mtt.jaapmala.domain.repository

import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.model.DailyGoalTarget
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {

    fun getActiveGoals(): Flow<List<DailyGoal>>

    fun getActiveGoalForJaap(jaapId: Int): Flow<DailyGoal?>

    suspend fun getGoalById(goalId: Int): DailyGoal?

    suspend fun insert(goal: DailyGoal): Long

    suspend fun update(goal: DailyGoal)

    suspend fun delete(goal: DailyGoal)

    suspend fun deactivateGoal(goalId: Int)
    // Target history
    suspend fun getTargetForDate(
        dailyGoalId: Int,
        date: String
    ): DailyGoalTarget?

    fun getTargetHistory(
        dailyGoalId: Int
    ): Flow<List<DailyGoalTarget>>

    suspend fun getCurrentTarget(
        dailyGoalId: Int
    ): DailyGoalTarget?

    suspend fun createTarget(
        target: DailyGoalTarget
    )

    suspend fun closeTargetPeriod(
        targetId: Int,
        effectiveTo: String
    )
    suspend fun createDailyGoal(
        jaapId: Int,
        targetMalas: Int,
        effectiveFrom: String
    )
}