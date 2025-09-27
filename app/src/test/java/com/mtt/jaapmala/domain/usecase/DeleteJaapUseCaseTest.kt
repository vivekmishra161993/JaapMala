package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteJaapUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var useCase: DeleteJaapUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DeleteJaapUseCase(repository)
    }

    @Test
    fun `invoke should call repository deleteJaap`() = runTest {
        // Arrange
        val jaap = JaapEntity(
            id = 1,
            name = "Gayatri Mantra",
            date = "2025-09-12",
            malaSize = 108,
            todayCount = 10,
            todayMalaCount = 1,
            lifetimeCount = 100,
            lifetimeMalaCount = 1,
            count = 2
        )

        // Act
        useCase(jaap)

        // Assert
        coVerify(exactly = 1) { repository.deleteJaap(jaap) }
    }
}
