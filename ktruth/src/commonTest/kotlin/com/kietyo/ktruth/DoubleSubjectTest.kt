package com.kietyo.ktruth

import kotlin.test.*

internal class DoubleSubjectTest {
    @Test
    fun assertTest1() {
        testAssertFails<AssertionError> {
            assertThat(2.0).isEqualTo(3.0)
        }
    }

    @Test
    fun assertTest2() {
        assertThat(2.0).isEqualTo(2.0)
    }
}