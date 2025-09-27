package com.mtt.jaapmala.data.repository

import app.cash.turbine.test
import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class JaapHistoryRepositoryImplTest {

    private lateinit var dao: JaapHistoryDao
    private lateinit var repository: JaapHistoryRepositoryImpl

    @Before
    fun setup() {
        dao = mockk()
        repository = JaapHistoryRepositoryImpl(dao)
    }

    @Test
    fun `insertOrUpdateHistory calls dao with correct entity`() = runTest {
        // Arrange
        coEvery { dao.insertOrUpdate(any()) } just runs

        val jaapId = 1
        val date = "2025-09-15"
        val count = 108
        val malaCount = 1

        val expectedEntity = JaapHistoryEntity(
            jaapId = jaapId,
            date = date,
            count = count,
            malaCount = malaCount
        )

        // Act
        repository.insertOrUpdateHistory(jaapId, date, count, malaCount)

        // Assert
        coVerify { dao.insertOrUpdate(expectedEntity) }
    }

    @Test
    fun `getHistoryForJaap returns flow from dao`() = runTest {
        // Arrange
        val jaapId = 1
        val history = listOf(
            JaapHistoryEntity(jaapId, "2025-09-15", count = 108, malaCount = 1),
            JaapHistoryEntity(jaapId, "2025-09-16", count = 216, malaCount = 2)
        )

        coEvery { dao.getHistoryForJaap(jaapId) } returns flowOf(history)

        // Act + Assert
        repository.getHistoryForJaap(jaapId).test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals(history, items)
            awaitComplete()
        }
    }
}
