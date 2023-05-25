package com.xenotactic.ecs

import kotlin.reflect.KClass

interface IEntity {
    operator fun <T : Any> get(klass: KClass<T>): T
}

abstract class AbstractEntity(): IEntity {
    protected abstract val componentMap: Map<KClass<out Any>, Any>
    val numComponents get() = componentMap.size
    val allComponents get() = componentMap.values
    inline fun <reified T: Any> containsComponentType(): Boolean {
        return containsComponentType(T::class)
    }
    fun containsComponentType(klass: KClass<out Any>): Boolean {
        return componentMap.containsKey(klass)
    }
    fun containsComponentTypes(vararg klass: KClass<out Any>): Boolean {
        return klass.all {
            componentMap.containsKey(it)
        }
    }
    override operator fun <T : Any> get(klass: KClass<T>): T {
        return componentMap[klass]!! as T
    }
}

