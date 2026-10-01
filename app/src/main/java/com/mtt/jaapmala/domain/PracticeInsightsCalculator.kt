package com.mtt.jaapmala.domain

import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.model.ai.PracticeInsights
import com.mtt.jaapmala.domain.model.ai.PracticeTrend
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Practice insights calculator
 *
 * @constructor Creates a new PracticeInsightsCalculator
 */
class PracticeInsightsCalculator @Inject constructor() {
    /**
     * Calculates
     *
     * @param history
     * @param startDate the start date
     * @param endDate the end date
     * @return the practice insights
     */
    fun calculate(
        history: List<JaapHistory>,
        startDate: LocalDate,
        endDate: LocalDate
    ): PracticeInsights {

        val historyByDate = history.associateBy {
            LocalDate.parse(it.date)
        }

        val periodDays =
            ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

        val totalMalas =
            history.sumOf { it.malaCount }

        val activeDays =
            history.count { it.malaCount > 0 }

        val averageMalasPerDay =
            if (periodDays > 0) {
                totalMalas.toFloat() / periodDays
            } else {
                0f
            }

        val currentStreak =
            calculateCurrentStreak(
                historyByDate = historyByDate,
                endDate = endDate
            )

        val longestStreak =
            calculateLongestStreak(
                historyByDate = historyByDate,
                startDate = startDate,
                endDate = endDate
            )

        val trend =
            calculateTrend(
                historyByDate = historyByDate,
                startDate = startDate,
                endDate = endDate
            )

        return PracticeInsights(
            periodDays = periodDays,
            totalMalas = totalMalas,
            averageMalasPerDay = averageMalasPerDay,
            activeDays = activeDays,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            trend = trend
        )
    }

    /**
     * Calculates the current streak
     *
     * @param historyByDate the history by date
     * @param endDate the end date
     * @return the int
     */
    private fun calculateCurrentStreak(
        historyByDate: Map<LocalDate, JaapHistory>,
        endDate: LocalDate
    ): Int {

        var date = endDate
        var streak = 0

        while (true) {

            val history = historyByDate[date]

            if (history?.malaCount?.let { it > 0 } != true) {
                break
            }

            streak++
            date = date.minusDays(1)
        }

        return streak
    }

    /**
     * Calculates the longest streak
     *
     * @param historyByDate the history by date
     * @param startDate the start date
     * @param endDate the end date
     * @return the int
     */
    private fun calculateLongestStreak(
        historyByDate: Map<LocalDate, JaapHistory>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {

        var longest = 0
        var current = 0
        var date = startDate

        while (!date.isAfter(endDate)) {

            val history = historyByDate[date]

            if (history?.malaCount?.let { it > 0 } == true) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 0
            }

            date = date.plusDays(1)
        }

        return longest
    }

    /**
     * Calculates the trend
     *
     * @param historyByDate the history by date
     * @param startDate the start date
     * @param endDate the end date
     * @return the practice trend
     */
    private fun calculateTrend(
        historyByDate: Map<LocalDate, JaapHistory>,
        startDate: LocalDate,
        endDate: LocalDate
    ): PracticeTrend {

        val totalDays =
            ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

        if (totalDays < 14) {
            return PracticeTrend.STABLE
        }

        val midpoint = totalDays / 2

        val firstHalfEnd =
            startDate.plusDays(midpoint - 1L)

        val secondHalfStart =
            firstHalfEnd.plusDays(1)

        val firstHalfDays =
            ChronoUnit.DAYS
                .between(startDate, firstHalfEnd)
                .toInt() + 1

        val secondHalfDays =
            ChronoUnit.DAYS
                .between(secondHalfStart, endDate)
                .toInt() + 1

        val firstHalfMalas =
            sumMalas(
                historyByDate,
                startDate,
                firstHalfEnd
            )

        val secondHalfMalas =
            sumMalas(
                historyByDate,
                secondHalfStart,
                endDate
            )

        val firstAverage =
            firstHalfMalas.toFloat() / firstHalfDays

        val secondAverage =
            secondHalfMalas.toFloat() / secondHalfDays

        return when {
            secondAverage > firstAverage -> PracticeTrend.IMPROVING
            secondAverage < firstAverage -> PracticeTrend.DECLINING
            else -> PracticeTrend.STABLE
        }
    }

    /**
     * Calculates the malas
     *
     * @param historyByDate the history by date
     * @param startDate the start date
     * @param endDate the end date
     * @return the int
     */
    private fun sumMalas(
        historyByDate: Map<LocalDate, JaapHistory>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {

        var total = 0
        var date = startDate

        while (!date.isAfter(endDate)) {
            total += historyByDate[date]?.malaCount ?: 0
            date = date.plusDays(1)
        }

        return total
    }
}