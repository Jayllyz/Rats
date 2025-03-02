package com.rats

import com.rats.utils.prettyDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DateTest {
    @Test
    fun `prettyDate should format date correctly`() {
        val inputDate = "2023-05-15"

        val result = prettyDate(inputDate)

        assertEquals("15 mai 2023", result)
    }

    @Test
    fun `prettyDate should handle first day of month correctly`() {
        val inputDate = "2023-01-01"

        val result = prettyDate(inputDate)

        assertEquals("1 janvier 2023", result)
    }

    @Test
    fun `prettyDate should handle last day of month correctly`() {
        val inputDate = "2023-12-31"

        val result = prettyDate(inputDate)

        assertEquals("31 décembre 2023", result)
    }

    @Test
    fun `prettyDate should handle leap year correctly`() {
        val inputDate = "2024-02-29"

        val result = prettyDate(inputDate)

        assertEquals("29 février 2024", result)
    }

    @Test
    fun `prettyDate should return null for invalid date format`() {
        val invalidDate = "2023/05/15"

        val result = prettyDate(invalidDate)

        assertNull(result)
    }

    @Test
    fun `prettyDate should return null for invalid date`() {
        val invalidDate = "2023-13-45" // Invalid month and day

        val result = prettyDate(invalidDate)

        assertNull(result)
    }

    @Test
    fun `prettyDate should handle malformed date gracefully`() {
        val malformedDate = "abcd-ef-gh"

        val result = prettyDate(malformedDate)

        assertNull(result)
    }

    @Test
    fun `prettyDate should handle empty string gracefully`() {
        val emptyDate = ""

        val result = prettyDate(emptyDate)

        assertNull(result)
    }
}
