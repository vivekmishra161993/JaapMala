package com.mtt.jaapmala.domain

import com.google.common.truth.Truth.assertThat
import com.mtt.jaapmala.data.local.entity.JaapEntity
import org.junit.Before
import org.junit.Test

class JaapCountCalculatorTest {

    private lateinit var calculator: JaapCountCalculator
    private lateinit var baseJaap: JaapEntity

    @Before
    fun setup() {
        calculator = JaapCountCalculator()
        baseJaap = JaapEntity(
            id = 1,
            name = "Gayatri Mantra",
            date = "2025-09-12",
            malaSize = 108,
            todayCount = 0,
            todayMalaCount = 0,
            lifetimeCount = 0,
            lifetimeMalaCount = 0,
            count = 0
        )
    }

    @Test
    fun `when addedCount is less than malaSize, should update todayCount and UI count only`() {
        val updated = calculator.calculateNewCounts(baseJaap, 50)

        assertThat(updated.todayCount).isEqualTo(50)
        assertThat(updated.todayMalaCount).isEqualTo(0) // not enough for a full mala
        assertThat(updated.lifetimeCount).isEqualTo(50)
        assertThat(updated.lifetimeMalaCount).isEqualTo(0)
        assertThat(updated.count).isEqualTo(50) // same as todayCount
    }

    @Test
    fun `when addedCount completes exactly one mala, should reset UI count`() {
        val updated = calculator.calculateNewCounts(baseJaap, 108)

        assertThat(updated.todayCount).isEqualTo(108)
        assertThat(updated.todayMalaCount).isEqualTo(1)
        assertThat(updated.lifetimeCount).isEqualTo(108)
        assertThat(updated.lifetimeMalaCount).isEqualTo(1)
        assertThat(updated.count).isEqualTo(0) // resets after full mala
    }

    @Test
    fun `when addedCount exceeds malaSize, should carry remainder into UI count`() {
        val updated = calculator.calculateNewCounts(baseJaap, 120)

        assertThat(updated.todayCount).isEqualTo(120)
        assertThat(updated.todayMalaCount).isEqualTo(1) // 120 ÷ 108 = 1
        assertThat(updated.lifetimeCount).isEqualTo(120)
        assertThat(updated.lifetimeMalaCount).isEqualTo(1)
        assertThat(updated.count).isEqualTo(12) // remainder
    }

    @Test
    fun `should accumulate values when jaap already has progress`() {
        val progressedJaap = baseJaap.copy(
            todayCount = 50,
            todayMalaCount = 0,
            lifetimeCount = 200,
            lifetimeMalaCount = 1,
            count = 50
        )

        val updated = calculator.calculateNewCounts(progressedJaap, 70)

        assertThat(updated.todayCount).isEqualTo(120) // 50 + 70
        assertThat(updated.todayMalaCount).isEqualTo(1) // 120 ÷ 108 = 1
        assertThat(updated.lifetimeCount).isEqualTo(270) // 200 + 70
        assertThat(updated.lifetimeMalaCount).isEqualTo(1 + (70 / 108)) // still 1
        assertThat(updated.count).isEqualTo(12) // 120 % 108
    }
}
