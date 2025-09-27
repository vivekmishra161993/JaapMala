package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateJaapUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var updateJaapUseCase: UpdateJaapUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed so we don’t need to stub methods
        updateJaapUseCase = UpdateJaapUseCase(repository)
    }

    @Test
    fun `invoke should call repository updateJaap with correct entity`() = runTest {
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

        // Act
        updateJaapUseCase(mantra)

        // Assert
        coVerify(exactly = 1) { repository.updateJaap(mantra) }
    }
}
