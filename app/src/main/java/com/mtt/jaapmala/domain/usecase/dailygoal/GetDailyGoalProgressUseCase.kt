package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.model.DailyGoalDayStatus
import com.mtt.jaapmala.domain.model.DailyGoalProgress
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDailyGoalProgressUseCase @Inject constructor(
    private val dailyGoalRepository: DailyGoalRepository,
    private val jaapHistoryRepository: JaapHistoryRepository
) {

    operator fun invoke(
        jaapId: Int,
        date: String
    ): Flow<DailyGoalProgress?> {

        return combine(
            dailyGoalRepository.getActiveGoalForJaap(jaapId),
            jaapHistoryRepository.getHistoryForDate(
                jaapId = jaapId,
                date = date
            )
        ) { goal, history ->

            if (goal == null) {
                return@combine null
            }

            val completedMalas = history?.malaCount ?: 0

            val progress =
                (completedMalas.toFloat() / goal.targetMalas)
                    .coerceIn(0f, 1f)

            val status = when {
                completedMalas >= goal.targetMalas ->
                    DailyGoalDayStatus.COMPLETED

                completedMalas > 0 ->
                    DailyGoalDayStatus.PARTIAL

                else ->
                    DailyGoalDayStatus.MISSED
            }

            DailyGoalProgress(
                goalId = goal.id,
                jaapId = goal.jaapId,
                targetMalas = goal.targetMalas,
                completedMalas = completedMalas,
                remainingMalas =
                    (goal.targetMalas - completedMalas)
                        .coerceAtLeast(0),
                progress = progress,
                status = status
            )
        }
    }
}