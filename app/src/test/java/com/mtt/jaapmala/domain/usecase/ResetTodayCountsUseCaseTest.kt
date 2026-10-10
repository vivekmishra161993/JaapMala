package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.domain.usecase.jaap.ResetTodayCountsUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ResetTodayCountsUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var useCase: ResetTodayCountsUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = ResetTodayCountsUseCase(repository)
    }

    @Test
    fun `invoke should call repository resetTodayCountsIfNeeded`() = runTest {
        // Act
        useCase()
        // Assert
        coVerify(exactly = 1) { repository.resetTodayCountsIfNeeded() }
    }
}
