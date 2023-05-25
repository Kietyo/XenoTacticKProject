package com.kietyo.ktruth

import kotlin.test.assertTrue

class StringSubject(val actual: String) {
    fun isEqualTo(expected: CharSequence) {
        assertThat(actual as Any).isEqualTo(expected)
    }
    fun contains(expected: CharSequence) {
        assertTrue("""
            Actual string: `$actual` 
            Does not contain expected string: `$expected`
        """.trimIndent()) {
            actual.contains(expected)
        }
    }
}