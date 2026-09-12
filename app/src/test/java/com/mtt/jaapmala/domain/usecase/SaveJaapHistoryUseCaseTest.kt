package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.domain.usecase.jaap.SaveJaapHistoryUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveJaapHistoryUseCaseTest {

    private lateinit var repository: JaapHistoryRepository
    private lateinit var useCase: SaveJaapHistoryUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = SaveJaapHistoryUseCase(repository)
    }

    @Test
    fun `invoke should call repository insertOrUpdateHistory with correct params`() = runTest {
        // Arrange
        val jaapId = 1
        val date = "2025-09-12"
        val count = 54
        val malaCount = 2

        // Act
        useCase(jaapId, date, count, malaCount)

        // Assert
        coVerify(exactly = 1) {
            repository.insertOrUpdateHistory(
                jaapId = jaapId,
                date = date,
                count = count,
                malaCount = malaCount
            )
        }
    }
}
