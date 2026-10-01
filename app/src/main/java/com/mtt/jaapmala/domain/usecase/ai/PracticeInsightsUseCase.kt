package com.mtt.jaapmala.domain.usecase.ai
import com.mtt.jaapmala.domain.PracticeInsightsCalculator
import com.mtt.jaapmala.domain.model.ai.PracticeInsights
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

/**
 * Returns the practice insights use case
 *
 * @constructor Creates a new GetPracticeInsightsUseCase
 * @property jaapHistoryRepository the jaap history repository
 * @property calculator
 */
class GetPracticeInsightsUseCase @Inject constructor(
    private val jaapHistoryRepository: JaapHistoryRepository,
    private val calculator: PracticeInsightsCalculator
) {
    /**
     * Handles
     *
     * @param jaapId the jaap id
     * @param periodDays the period days
     * @return a cold [Flow] emitting [PracticeInsights] values
     */
    operator fun invoke(
        jaapId: Int,
        periodDays: Int = 30
    ): Flow<PracticeInsights> {

        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(periodDays - 1L)

        return jaapHistoryRepository
            .getHistoryBetweenDates(
                jaapId = jaapId,
                startDate = startDate.toString(),
                endDate = endDate.toString()
            )
            .map { history ->

                calculator.calculate(
                    history = history,
                    startDate = startDate,
                    endDate = endDate
                )
            }
    }
}