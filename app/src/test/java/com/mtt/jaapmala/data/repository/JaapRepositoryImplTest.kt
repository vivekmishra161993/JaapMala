package com.mtt.jaapmala.data.repository

import com.google.common.truth.Truth.assertThat
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.entity.JaapEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test


class JaapRepositoryImplTest {
    private val dao = mockk<JaapDao>()
    private lateinit var repository: JaapRepositoryImpl
    @Before
    fun setUp() {
        repository = JaapRepositoryImpl(dao)
    }

    @Test
    fun `updateJaap calls dao`() = runTest {
        val mantra = JaapEntity(id = 1, name = "Gayatri", date = "2025-09-15", malaSize = 108)
        coEvery { dao.updateJaap(any()) } just Runs
        repository.updateJaap(mantra)

        coVerify { dao.updateJaap(mantra) }
    }

    @Test
    fun `insertMantra calls dao and returns dto`() = runTest {
        val mantra = JaapEntity(id = 1, name = "Gayatri", date = "2025-09-15", malaSize = 108)
        coEvery { dao.insertMantra(mantra.copy(id = 0)) } returns 1L

        val result = repository.insertMantra(mantra.name, mantra.date, mantra.malaSize)
        coVerify { dao.insertMantra(mantra.copy(id = 0)) }
        assertEquals(mantra.name, result.name)
        assertEquals(mantra.date, result.date)
        assertEquals(mantra.malaSize, result.malaSize)

    }

    @Test
    fun `getAllMantras returns flow from dao`() = runTest {
// Arrange
        val mantraList = listOf(
            JaapEntity(id = 1, name = "Gayatri", date = "2025-09-15", malaSize = 108),
            JaapEntity(id = 2, name = "Mahamrityunjaya", date = "2025-09-15", malaSize = 108)
        )
        every { dao.getAllMantras() } returns flowOf(mantraList)

        // Act
        val result = repository.getAllMantras().first()

        // Assert
        assertThat(result).isEqualTo(mantraList)
        verify { dao.getAllMantras() }
    }

    @Test
    fun `getMantra returns flow from dao`() = runTest {
        val mantra = JaapEntity(id = 1, name = "Gayatri", date = "2025-09-15", malaSize = 108)
        every { dao.getMantra(1) } returns flowOf(mantra)

        val result = repository.getMantra(1).first()

        assertThat(result).isEqualTo(mantra)
        verify { dao.getMantra(1) }
    }

    @Test
    fun `deleteJaap calls dao`() = runTest {
        val mantra = JaapEntity(id = 1, name = "Gayatri", date = "2025-09-15", malaSize = 108)
        coEvery { dao.deleteJaap(mantra) } just Runs

        repository.deleteJaap(mantra)

        coVerify { dao.deleteJaap(mantra) }
    }

    @After
    fun destroy(){
        unmockkAll()
    }
}