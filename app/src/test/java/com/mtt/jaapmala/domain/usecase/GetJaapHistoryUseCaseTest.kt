package com.mtt.jaapmala.domain.usecase

import app.cash.turbine.test
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.domain.usecase.jaap.GetJaapHistoryUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetJaapHistoryUseCaseTest {

    private lateinit var repository: JaapHistoryRepository
    private lateinit var useCase: GetJaapHistoryUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetJaapHistoryUseCase(repository)
    }

    @Test
    fun `invoke should return history list from repository`() = runTest {
        // Arrange
        val jaapId = 1
        val historyList = listOf(
            JaapHistoryEntity(jaapId = 1, date = "2025-09-10", count = 108, malaCount = 1),
            JaapHistoryEntity(jaapId = 1, date = "2025-09-11", count = 216, malaCount = 2)
        )

        every { repository.getHistoryForJaap(jaapId) } returns flowOf(historyList)

        // Act & Assert
        useCase(jaapId).test {
            val result = awaitItem()
            assertEquals(historyList, result)
            awaitComplete()
        }
    }
}
