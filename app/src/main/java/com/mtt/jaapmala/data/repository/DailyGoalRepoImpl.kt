package com.mtt.jaapmala.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.mtt.jaapmala.data.local.dao.DailyGoalDao
import com.mtt.jaapmala.data.local.dao.DailyGoalTargetDao
import com.mtt.jaapmala.data.local.db.JaapDatabase
import com.mtt.jaapmala.data.local.entity.DailyGoalEntity
import com.mtt.jaapmala.data.local.entity.DailyGoalTargetEntity
import com.mtt.jaapmala.data.mapper.toDomain
import com.mtt.jaapmala.data.mapper.toEntity
import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.model.DailyGoalTarget
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyGoalRepoImpl @Inject constructor(
    private val database: JaapDatabase,
    private val dailyGoalDao: DailyGoalDao,
    private val dailyGoalTargetDao: DailyGoalTargetDao
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

    override suspend fun insert(goal: DailyGoal): Long {
       return dailyGoalDao.insert(goal.toEntity())
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

    override suspend fun getTargetForDate(
        dailyGoalId: Int,
        date: String
    ): DailyGoalTarget? {
        return dailyGoalTargetDao
            .getTargetForDate(dailyGoalId, date)
            ?.toDomain()
    }

    override fun getTargetHistory(dailyGoalId: Int): Flow<List<DailyGoalTarget>> {
        return dailyGoalTargetDao
            .getTargetHistory(dailyGoalId)
            .map { targets ->
                targets.map { it.toDomain() }
            }
    }

    override suspend fun getCurrentTarget(dailyGoalId: Int): DailyGoalTarget? {
        return dailyGoalTargetDao
            .getCurrentTarget(dailyGoalId)
            ?.toDomain()
    }

    override suspend fun createTarget(target: DailyGoalTarget) {
        dailyGoalTargetDao.insert(
            target.toEntity()
        )    }

    override suspend fun closeTargetPeriod(targetId: Int, effectiveTo: String) {
        dailyGoalTargetDao.closeTargetPeriod(
            targetId = targetId,
            effectiveTo = effectiveTo
        )    }

    override suspend fun createDailyGoal(
        jaapId: Int,
        targetMalas: Int,
        effectiveFrom: String
    ) {
        database.withTransaction {

            Log.d("DailyGoal", "1. Starting: jaapId=$jaapId")

            dailyGoalDao
                .getActiveGoalForJaapOnce(jaapId)
                ?.let { existingGoal ->
                    Log.d(
                        "DailyGoal",
                        "2. Deactivating existing goal=${existingGoal.id}"
                    )

                    dailyGoalDao.deactivateGoal(existingGoal.id)
                }

            Log.d("DailyGoal", "3. Inserting daily goal")

            val goalId = dailyGoalDao.insert(
                DailyGoalEntity(
                    jaapId = jaapId,
                    isActive = true
                )
            ).toInt()

            Log.d("DailyGoal", "4. Created daily goal id=$goalId")

            Log.d("DailyGoal", "5. Inserting target")

            dailyGoalTargetDao.insert(
                DailyGoalTargetEntity(
                    dailyGoalId = goalId,
                    targetMalas = targetMalas,
                    effectiveFrom = effectiveFrom,
                    effectiveTo = null
                )
            )

            Log.d("DailyGoal", "6. Target inserted successfully")
        }
    }


}