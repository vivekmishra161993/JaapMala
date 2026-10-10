package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mtt.jaapmala.data.local.entity.DailyGoalTargetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalTargetDao {
    @Insert
    suspend fun insert(target: DailyGoalTargetEntity)

    @Update
    suspend fun update(target: DailyGoalTargetEntity)
    @Query("""
        SELECT * FROM daily_goal_targets
        WHERE dailyGoalId = :dailyGoalId
        AND effectiveFrom <= :date
        AND (
            effectiveTo IS NULL
            OR effectiveTo >= :date
        )
        LIMIT 1
    """)
    suspend fun getTargetForDate(
        dailyGoalId: Int,
        date: String
    ): DailyGoalTargetEntity?

    @Query("""
        SELECT * FROM daily_goal_targets
        WHERE dailyGoalId = :dailyGoalId
        ORDER BY effectiveFrom ASC
    """)
    fun getTargetHistory(
        dailyGoalId: Int
    ): Flow<List<DailyGoalTargetEntity>>

    @Query("""
        SELECT * FROM daily_goal_targets
        WHERE dailyGoalId = :dailyGoalId
        AND effectiveTo IS NULL
        LIMIT 1
    """)
    suspend fun getCurrentTarget(
        dailyGoalId: Int
    ): DailyGoalTargetEntity?

    @Query("""
        UPDATE daily_goal_targets
        SET effectiveTo = :effectiveTo
        WHERE id = :targetId
    """)
    suspend fun closeTargetPeriod(
        targetId: Int,
        effectiveTo: String
    )
}