package com.mtt.jaapmala.util

import junit.framework.TestCase.assertEquals
import org.junit.Test

class DateUtilsTest {
    @Test
    fun `formatDate should convert yyyy-MM-dd to dd-MM-yyyy`() {
        val input = "2025-08-02"
        val expected = "02-08-2025"
        val actual = DateUtils.formatDate(input)
        assertEquals(expected, actual)
    }
}