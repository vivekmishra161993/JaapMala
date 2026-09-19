package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.model.DailyGoalDayStatus
import com.mtt.jaapmala.domain.model.DailyGoalProgress
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.util.DateUtils.getTodayDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDailyGoalProgressUseCase @Inject constructor(
    private val dailyGoalRepository: DailyGoalRepository,
    private val jaapHistoryRepository: JaapHistoryRepository
) {

    operator fun invoke(
        goal: DailyGoal,
        date: String
    ): Flow<DailyGoalProgress> {

        return flow {

            val target =
                dailyGoalRepository.getTargetForDate(
                    dailyGoalId = goal.id,
                    date = date
                )

            val history =
                jaapHistoryRepository
                    .getHistoryForDate(
                        jaapId = goal.jaapId,
                        date = date
                    )
                    .first()

            val targetMalas = target?.targetMalas ?: 0
            val completedMalas = history?.malaCount ?: 0

            val remainingMalas =
                (targetMalas - completedMalas).coerceAtLeast(0)

            val progress =
                if (targetMalas > 0) {
                    (completedMalas.toFloat() / targetMalas)
                        .coerceIn(0f, 1f)
                } else {
                    0f
                }

            val status = when {
                targetMalas <= 0 ->
                    DailyGoalDayStatus.PENDING

                completedMalas >= targetMalas ->
                    DailyGoalDayStatus.COMPLETED

                completedMalas > 0 ->
                    DailyGoalDayStatus.PARTIAL

                date == getTodayDate() ->
                    DailyGoalDayStatus.PENDING

                else ->
                    DailyGoalDayStatus.MISSED
            }

            emit(
                DailyGoalProgress(
                    goalId = goal.id,
                    jaapId = goal.jaapId,
                    targetMalas = targetMalas,
                    completedMalas = completedMalas,
                    remainingMalas = remainingMalas,
                    progress = progress,
                    status = status
                )
            )
        }
    }
}