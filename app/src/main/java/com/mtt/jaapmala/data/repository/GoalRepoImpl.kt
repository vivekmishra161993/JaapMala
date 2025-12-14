package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.GoalDao
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoalRepositoryImpl(private val goalDao: GoalDao) : GoalRepository {
    override suspend fun addGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: GoalEntity) {
        goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    override fun getAllGoals(): Flow<List<GoalEntity>> = flow {
        emit(goalDao.getAllGoals())
    }

    override suspend fun getGoalById(goalId: Int): GoalEntity? {
        return goalDao.getGoalById(goalId)
    }

    override suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity> {
        return goalDao.getGoalsByJaapId(jaapId)
    }
}