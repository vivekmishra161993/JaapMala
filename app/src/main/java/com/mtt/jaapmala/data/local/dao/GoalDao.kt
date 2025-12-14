package com.mtt.jaapmala.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mtt.jaapmala.data.local.entity.GoalEntity

@Dao
interface GoalDao {
    // 🔹 Insert a new goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    // 🔹 Update existing goal progress or details
    @Update
    suspend fun updateGoal(goal: GoalEntity)

    // 🔹 Delete a goal
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // 🔹 Fetch all goals
    @Query("SELECT * FROM goals ORDER BY id DESC")
    suspend fun getAllGoals(): List<GoalEntity>

    // 🔹 Fetch a goal by ID
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?

    // 🔹 Fetch all goals linked to a specific Jaap
    @Query("SELECT * FROM goals WHERE jaapId = :jaapId")
    suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity>

    // 🔹 Reset daily goals (optional helper)
    @Query("UPDATE goals SET currentMalas = 0 WHERE endDate IS NULL")
    suspend fun resetDailyGoals()
}
