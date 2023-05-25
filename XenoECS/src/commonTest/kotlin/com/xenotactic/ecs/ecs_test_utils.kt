package com.xenotactic.ecs

object ObjectComponent

data class TestComponent(val value: String)
data class TestComponent2(val value: String)

sealed class TestSealedClass {
    class RegularClassChild(
        val value: String
    ) : TestSealedClass()

    data class DataClassChild(
        val value: String
    ) : TestSealedClass()

    object ObjectClassChild: TestSealedClass()
}

enum class TestEnumClass {
    ENUM_1,
    ENUM_2,
    ENUM_3;
}