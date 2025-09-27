package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.JaapCountCalculator
import com.mtt.jaapmala.domain.repository.JaapRepository
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateJaapManuallyUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var calculator: JaapCountCalculator
    private lateinit var useCase: UpdateJaapManuallyUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        calculator = mockk()
        useCase = UpdateJaapManuallyUseCase(repository, calculator)
    }

    @Test
    fun `invoke should update jaap when mantra exists`() = runTest {
        // Arrange
        val mantra = JaapEntity(
            id = 1,
            name = "Gayatri Mantra",
            date = "2025-09-12",
            malaSize = 108,
            todayCount = 20,
            todayMalaCount = 2,
            lifetimeCount = 400,
            lifetimeMalaCount = 3,
            count = 5
        )
        val updatedMantra = mantra.copy(todayCount = 25)

        coEvery { repository.getMantra(1) } returns flowOf(mantra)
        every { calculator.calculateNewCounts(mantra, 5) } returns updatedMantra

        // Act
        useCase(1, 5)

        // Assert
        coVerify(exactly = 1) { repository.updateJaap(updatedMantra) }
    }

    @Test
    fun `invoke should do nothing when mantra does not exist`() = runTest {
        // Arrange
        coEvery { repository.getMantra(1) } returns emptyFlow()

        // Act
        useCase(1, 10)

        // Assert
        coVerify(exactly = 0) { repository.updateJaap(any()) }
        verify { calculator wasNot Called }
    }
}
