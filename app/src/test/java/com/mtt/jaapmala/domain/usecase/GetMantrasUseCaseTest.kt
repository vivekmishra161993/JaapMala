package com.mtt.jaapmala.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.domain.usecase.jaap.GetMantrasUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetMantrasUseCaseTest {

    private lateinit var repository: JaapRepository
    private lateinit var getMantrasUseCase: GetMantrasUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getMantrasUseCase = GetMantrasUseCase(repository)
    }

    @Test
    fun `invoke should return list of mantras`() = runTest {
        // Arrange
        val mantraList = listOf(
            JaapEntity(
                id = 1,
                name = "Gayatri Mantra",
                count = 108,
                malaSize = 108,
                date = "2025-09-12" // ✅ date included
            ),
            JaapEntity(
                id = 2,
                name = "Mahamrityunjaya",
                count = 216,
                malaSize = 108,
                date = "2025-09-12"
            )
        )

        coEvery { repository.getAllMantras() } returns flowOf(mantraList)

        // Act + Assert
        getMantrasUseCase().test {
            val result = awaitItem()
            assertThat(result).hasSize(2)
            assertThat(result[0].name).isEqualTo("Gayatri Mantra")
            assertThat(result[1].count).isEqualTo(216)
            assertThat(result[0].date).isEqualTo("2025-09-12")
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no mantras exist`() = runTest {
        // Arrange
        coEvery { repository.getAllMantras() } returns flowOf(emptyList())

        // Act + Assert
        getMantrasUseCase().test {
            val result = awaitItem()
            assertThat(result).isEmpty()
            awaitComplete()
        }
    }
}