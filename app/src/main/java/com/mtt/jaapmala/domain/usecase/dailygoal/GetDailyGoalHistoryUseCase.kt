package com.mtt.jaapmala.domain.usecase.dailygoal

import com.mtt.jaapmala.domain.model.DailyGoalDay
import com.mtt.jaapmala.domain.model.DailyGoalDayStatus
import com.mtt.jaapmala.domain.model.DailyGoalTarget
import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.repository.DailyGoalRepository
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.util.DateUtils.generateDateRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class GetDailyGoalHistoryUseCase @Inject constructor(
    private val dailyGoalRepository: DailyGoalRepository,
    private val jaapHistoryRepository: JaapHistoryRepository
) {

    operator fun invoke(
        goalId: Int
    ): Flow<List<DailyGoalDay>> = flow {

        val goal = dailyGoalRepository.getGoalById(goalId)

        if (goal == null) {
            emit(emptyList())
            return@flow
        }

        val targetHistory =
            dailyGoalRepository
                .getTargetHistory(goalId)
                .first()

        if (targetHistory.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        jaapHistoryRepository
            .getHistoryForJaap(goal.jaapId)
            .collect { historyList ->

                emit(
                    buildHistory(
                        targetHistory = targetHistory,
                        historyList = historyList
                    )
                )
            }
    }
}
private fun buildHistory(
    targetHistory: List<DailyGoalTarget>,
    historyList: List<JaapHistory>
): List<DailyGoalDay> {

    val historyByDate = historyList.associateBy { it.date }

    val today = LocalDate.now()
    val startDate = today.minusDays(29).toString()
    val endDate = today.toString()

    val dates = generateDateRange(
        startDate = startDate,
        endDate = endDate
    )

    return dates
        .sortedDescending()
        .mapNotNull { date ->

            val target = targetHistory.firstOrNull { target ->
                date >= target.effectiveFrom &&
                        (
                                target.effectiveTo == null ||
                                        date <= target.effectiveTo
                                )
            } ?: return@mapNotNull null

            val targetMalas = target.targetMalas

            val completedMalas =
                historyByDate[date]?.malaCount ?: 0

            val progress = (
                    completedMalas.toFloat() / targetMalas
                    ).coerceIn(0f, 1f)

            val status = when {
                completedMalas >= targetMalas ->
                    DailyGoalDayStatus.COMPLETED

                date == endDate && completedMalas > 0 ->
                    DailyGoalDayStatus.PARTIAL

                date == endDate ->
                    DailyGoalDayStatus.PENDING

                completedMalas > 0 ->
                    DailyGoalDayStatus.PARTIAL

                else ->
                    DailyGoalDayStatus.MISSED
            }

            DailyGoalDay(
                date = date,
                targetMalas = targetMalas,
                completedMalas = completedMalas,
                progress = progress,
                status = status
            )
        }
}