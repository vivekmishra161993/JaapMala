package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mtt.jaapmala.data.local.entity.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: DailyGoalEntity)

    @Update
    suspend fun update(goal: DailyGoalEntity)

    @Delete
    suspend fun delete(goal: DailyGoalEntity)

    @Query("""
        SELECT * FROM daily_goals
        WHERE isActive = 1
        ORDER BY startDate DESC
    """)
    fun getActiveGoals(): Flow<List<DailyGoalEntity>>

    @Query("""
        SELECT * FROM daily_goals
        WHERE jaapId = :jaapId
        AND isActive = 1
        LIMIT 1
    """)
    fun getActiveGoalForJaap(
        jaapId: Int
    ): Flow<DailyGoalEntity?>

    @Query("""
        SELECT * FROM daily_goals
        WHERE id = :goalId
        LIMIT 1
    """)
    suspend fun getGoalById(
        goalId: Int
    ): DailyGoalEntity?

    @Query("""
        UPDATE daily_goals
        SET isActive = 0
        WHERE id = :goalId
    """)
    suspend fun deactivateGoal(
        goalId: Int
    )
    @Query("""
    SELECT * FROM daily_goals
    WHERE jaapId = :jaapId
    AND isActive = 1
    LIMIT 1
""")
    suspend fun getActiveGoalForJaapOnce(
        jaapId: Int
    ): DailyGoalEntity?
}