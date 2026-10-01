package com.mtt.jaapmala.domain

import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.jaapmala.domain.model.ai.PracticeTrend
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PracticeInsightsCalculatorTest {

    private val calculator = PracticeInsightsCalculator()

    @Test
    fun `empty history returns zero insights`() {

        val startDate = LocalDate.of(2026, 9, 1)
        val endDate = LocalDate.of(2026, 9, 30)

        val result = calculator.calculate(
            history = emptyList(),
            startDate = startDate,
            endDate = endDate
        )

        assertEquals(30, result.periodDays)
        assertEquals(0, result.totalMalas)
        assertEquals(0f, result.averageMalasPerDay)
        assertEquals(0, result.activeDays)
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.longestStreak)
        assertEquals(PracticeTrend.STABLE, result.trend)
    }

    @Test
    fun `calculates total and average malas`() {

        val startDate = LocalDate.of(2026, 9, 1)
        val endDate = LocalDate.of(2026, 9, 30)

        val history = listOf<JaapHistory>(
            history("2026-09-05", 10),
            history("2026-09-10", 20),
            history("2026-09-15", 10)
        )

        val result = calculator.calculate(
            history = history,
            startDate = startDate,
            endDate = endDate
        )

        assertEquals(40, result.totalMalas)
        assertEquals(3, result.activeDays)
        assertEquals(40f / 30f, result.averageMalasPerDay)
    }

    @Test
    fun `calculates current streak`() {

        val startDate = LocalDate.of(2026, 9, 1)
        val endDate = LocalDate.of(2026, 9, 10)

        val history = listOf(
            history("2026-09-06", 5),
            history("2026-09-07", 5),
            history("2026-09-08", 5),
            history("2026-09-09", 5),
            history("2026-09-10", 5)
        )

        val result = calculator.calculate(
            history = history,
            startDate = startDate,
            endDate = endDate
        )

        assertEquals(5, result.currentStreak)
        assertEquals(5, result.longestStreak)
    }

    @Test
    fun `gap breaks current streak`() {

        val startDate = LocalDate.of(2026, 9, 1)
        val endDate = LocalDate.of(2026, 9, 10)

        val history = listOf(
            history("2026-09-05", 5),
            history("2026-09-07", 5),
            history("2026-09-08", 5),
            history("2026-09-09", 5),
            history("2026-09-10", 5)
        )

        val result = calculator.calculate(
            history = history,
            startDate = startDate,
            endDate = endDate
        )

        assertEquals(4, result.currentStreak)
        assertEquals(4, result.longestStreak)
    }

    @Test
    fun `calculates improving trend`() {

        val startDate = LocalDate.of(2026, 9, 1)
        val endDate = LocalDate.of(2026, 9, 30)

        val history = listOf(
            // First half
            history("2026-09-01", 5),
            history("2026-09-05", 5),

            // Second half
            history("2026-09-16", 10),
            history("2026-09-20", 10),
            history("2026-09-25", 10)
        )

        val result = calculator.calculate(
            history = history,
            startDate = startDate,
            endDate = endDate
        )

        assertEquals(
            PracticeTrend.IMPROVING,
            result.trend
        )
    }

    private fun history(
        date: String,
        malaCount: Int
    ) = JaapHistory(
        jaapId = 1,
        date = date,
        count = malaCount * 108,
        malaCount = malaCount
    )
}