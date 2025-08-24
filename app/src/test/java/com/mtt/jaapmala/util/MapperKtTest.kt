package com.mtt.jaapmala.util

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MapperKtTest {

    @Test
    fun `toMantraDto basic mapping`() {
        // Verify that all fields from JaapEntity are correctly mapped to MantraDto.
        // Arrange
        val jaapEntity = JaapEntity(
            id = 1,
            name = "Test Mantra",
            date = "2023-10-27",
            count = 10,
            todayCount = 108,
            todayMalaCount = 1,
            lifetimeCount = 1080,
            lifetimeMalaCount = 10,
            malaSize = 108
        )
        val dto = jaapEntity.toMantraDto()
        assertEquals(dto.id,jaapEntity.id)
        assertEquals(dto.name,jaapEntity.name)
        assertEquals(dto.date,jaapEntity.date)
        assertEquals(dto.currentCount,jaapEntity.count)
        assertEquals(dto.todayCount,jaapEntity.todayCount)
        assertEquals(dto.malaCount,jaapEntity.todayMalaCount)
        assertEquals(dto.lifetimeCount,jaapEntity.lifetimeCount)
        assertEquals(dto.lifetimeMalaCount,jaapEntity.lifetimeMalaCount)
        assertEquals(dto.malaSize,jaapEntity.malaSize)


    }

    @Test
    fun `toMantraDto mapping with null or default values`() {
        // Test how JaapEntity with null or default values for its fields is mapped to MantraDto. 
        // Arrange
        val jaapEntity = JaapEntity(
            id = 1,
            name = "Test Mantra",
            date = "2023-10-27",
        )
        val dto = jaapEntity.toMantraDto()
        assertEquals(dto.id,jaapEntity.id)
        assertEquals(dto.name,jaapEntity.name)
        assertEquals(dto.date,jaapEntity.date)
        assertEquals(dto.currentCount,jaapEntity.count)
        assertEquals(dto.todayCount,jaapEntity.todayCount)
        assertEquals(dto.malaCount,jaapEntity.todayMalaCount)
        assertEquals(dto.lifetimeCount,jaapEntity.lifetimeCount)
        assertEquals(dto.lifetimeMalaCount,jaapEntity.lifetimeMalaCount)
        assertEquals(dto.malaSize,jaapEntity.malaSize)
    }

    @Test
    fun `toMantraDto mapping with empty strings`() {
        // Check the mapping when 'name' in JaapEntity is an empty string.
        // Arrange
        val jaapEntity = JaapEntity(
            id = 1,
            name = "",
            date = "2023-10-27",
        )
        val dto = jaapEntity.toMantraDto()
        assertEquals(dto.id,jaapEntity.id)
        assertEquals(dto.name,jaapEntity.name)
        assertEquals(dto.date,jaapEntity.date)
        assertEquals(dto.currentCount,jaapEntity.count)
        assertEquals(dto.todayCount,jaapEntity.todayCount)
        assertEquals(dto.malaCount,jaapEntity.todayMalaCount)
        assertEquals(dto.lifetimeCount,jaapEntity.lifetimeCount)
        assertEquals(dto.lifetimeMalaCount,jaapEntity.lifetimeMalaCount)
        assertEquals(dto.malaSize,jaapEntity.malaSize)
    }


    @Test
    fun `toMantraDto mapping with large count values`() {
        // Test mapping with large integer values for count fields to ensure no overflow or truncation issues.
        // Arrange
        val jaapEntity = JaapEntity(
            id = 1,
            name = "Test Mantra",
            date = "2023-10-27",
            count = 100000,
            todayCount = 1080000,
            todayMalaCount = 100000,
            lifetimeCount = 108000000,
            lifetimeMalaCount = 10000000,
            malaSize = 108
        )
        val dto = jaapEntity.toMantraDto()
        assertEquals(dto.id,jaapEntity.id)
        assertEquals(dto.name,jaapEntity.name)
        assertEquals(dto.date,jaapEntity.date)
        assertEquals(dto.currentCount,jaapEntity.count)
        assertEquals(dto.todayCount,jaapEntity.todayCount)
        assertEquals(dto.malaCount,jaapEntity.todayMalaCount)
        assertEquals(dto.lifetimeCount,jaapEntity.lifetimeCount)
        assertEquals(dto.lifetimeMalaCount,jaapEntity.lifetimeMalaCount)
        assertEquals(dto.malaSize,jaapEntity.malaSize)
    }

    @Test
    fun `toJaapEntity basic mapping`() {
        // Verify that all fields from MantraDto are correctly mapped to JaapEntity.
        // Arrange
        val dto = MantraDto(
            id = 1,
            name = "Test Mantra",
            date = "2023-10-27",
            currentCount = 100000,
            todayCount = 1080000,
            malaCount = 100000,
            lifetimeCount = 108000000,
            lifetimeMalaCount = 10000000,
            malaSize = 108
        )
        val jaapEntity = dto.toJaapEntity()
        assertEquals(dto.id,jaapEntity.id)
        assertEquals(dto.name,jaapEntity.name)
        assertEquals(dto.date,jaapEntity.date)
        assertEquals(dto.currentCount,jaapEntity.count)
        assertEquals(dto.todayCount,jaapEntity.todayCount)
        assertEquals(dto.malaCount,jaapEntity.todayMalaCount)
        assertEquals(dto.lifetimeCount,jaapEntity.lifetimeCount)
        assertEquals(dto.lifetimeMalaCount,jaapEntity.lifetimeMalaCount)
        assertEquals(dto.malaSize,jaapEntity.malaSize)
    }

}