package com.mtt.jaapmala.data.repository

import com.mtt.jaapmala.data.local.dao.DailyGoalDao
import com.mtt.jaapmala.data.mapper.toDomain
import com.mtt.jaapmala.data.mapper.toEntity
import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyGoalRepoImpl @Inject constructor(
    private val dailyGoalDao: DailyGoalDao
) : DailyGoalRepository {

    override fun getActiveGoals(): Flow<List<DailyGoal>> {
        return dailyGoalDao.getActiveGoals()
            .map { goals ->
                goals.map { it.toDomain() }
            }
    }

    override fun getActiveGoalForJaap(
        jaapId: Int
    ): Flow<DailyGoal?> {
        return dailyGoalDao.getActiveGoalForJaap(jaapId)
            .map { it?.toDomain() }
    }

    override suspend fun getGoalById(
        goalId: Int
    ): DailyGoal? {
        return dailyGoalDao.getGoalById(goalId)?.toDomain()
    }

    override suspend fun insert(goal: DailyGoal) {
        dailyGoalDao.insert(goal.toEntity())
    }

    override suspend fun update(goal: DailyGoal) {
        dailyGoalDao.update(goal.toEntity())
    }

    override suspend fun delete(goal: DailyGoal) {
        dailyGoalDao.delete(goal.toEntity())
    }

    override suspend fun deactivateGoal(goalId: Int) {
        dailyGoalDao.deactivateGoal(goalId)
    }

    override suspend fun getActiveGoalForJaapOnce(jaapId: Int): DailyGoal? {
      return  dailyGoalDao.getActiveGoalForJaapOnce(jaapId)?.toDomain()
    }
}