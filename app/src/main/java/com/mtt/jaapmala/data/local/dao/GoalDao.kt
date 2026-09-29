package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.GoalWithJaapName
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    // 🔹 Insert a new goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    // 🔹 Update existing goal progress or details
    @Update
    suspend fun updateGoal(goal: List<GoalEntity>)


    // 🔹 Delete a goal
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // 🔹 Fetch all goals
    @Query("SELECT * FROM goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    // 🔹 Fetch a goal by ID
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?

    // 🔹 Fetch all goals linked to a specific Jaap
    @Query("SELECT * FROM goals WHERE jaapId = :jaapId")
    suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity>

    @Query("SELECT * FROM goals WHERE status = 'ACTIVE'")
    suspend fun getActiveGoals(): List<GoalEntity>

    @Query("SELECT * FROM goals WHERE jaapId = :jaapId AND status = 'ACTIVE'")
    suspend fun findActiveGoalByJaapId(jaapId: Int): List<GoalEntity>

    @Query("""
    SELECT g.*, j.name AS jaapName
    FROM goals g
    INNER JOIN jaaps j ON g.jaapId = j.id
""")
    fun getGoalsWithJaapName(): Flow<List<GoalWithJaapName>>

}
