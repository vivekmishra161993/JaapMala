package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.GoalDao
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.domain.repository.GoalRepository
import com.mtt.jaapmala.data.mapper.toUiModel
import com.mtt.presentation.ui.screens.goals.GoalUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GoalRepositoryImpl(private val goalDao: GoalDao) : GoalRepository {
    override suspend fun addGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    override suspend fun updateGoal(goal: List<GoalEntity>) {
        goalDao.updateGoal(goal)
    }

    override suspend fun deleteGoal(goal: GoalEntity) {
        goalDao.deleteGoal(goal)
    }

    override fun getAllGoals(): Flow<List<GoalUiModel>>  {
        return goalDao.getGoalsWithJaapName().map {
            list->list.map{it.toUiModel()}
        }
    }

    override suspend fun getGoalById(goalId: Int): GoalEntity? {
        return goalDao.getGoalById(goalId)
    }

    override suspend fun getGoalsByJaapId(jaapId: Int): List<GoalEntity> {
        return goalDao.getGoalsByJaapId(jaapId)
    }

    override suspend fun getActiveGoals(): List<GoalEntity> {
        return goalDao.getActiveGoals()
    }

    override suspend fun findActiveGoalByJaapId(jaapId: Int): List<GoalEntity> {
        return goalDao.findActiveGoalByJaapId(jaapId)
    }
}