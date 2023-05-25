package com.kietyo.ktruth

import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

fun assertThat(actual: Double) = DoubleSubject(actual)
fun assertThat(actual: Boolean) = BooleanSubject(actual)
fun assertThat(actual: String) = StringSubject(actual)
fun <T : Any> assertThat(actual: T) = AnySubject(actual)
fun <T : Any> assertThat(actual: Collection<T>) = CollectionSubject(actual)

inline fun <reified T: Throwable> testAssertFails(block: () -> Unit) {
    var failed = false
    try {
        block()
    } catch (throwable: Throwable) {
        failed = true
        assertIs<T>(throwable)
    }

    assertThat(failed).isTrue()
}

fun testAssertFailsWithMessage(expectedErrorMessage: String, block: () -> Unit) {
    try {
        block()
    } catch (assertionError: AssertionError) {
        assertEquals(expectedErrorMessage, assertionError.message,
            """
                Error messages not equivalent.
                Expected:
                $expectedErrorMessage
                Actual:
                ${assertionError.message}
            """.trimIndent())
    }
}