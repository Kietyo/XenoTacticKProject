package com.xenotactic.ecs

import kotlin.reflect.KClass

/**
 * A staging entity is an entity that has not yet been assigned an ID.
 *
 * It can be used as input into a world to insert the "entity" into the world.
 */
data class StagingEntity(
    override val componentMap: MutableMap<KClass<out Any>, Any> = mutableMapOf()
) : AbstractEntity() {
    constructor(block: StagingEntity.() -> Unit) : this() {
        block(this)
    }

    fun <T : Any> addOrReplaceComponentForEntity(component: T) {
        componentMap[component::class] = component
    }

    fun <T : Any> addIfNotExistsForEntity(component: T) {
        if (!componentMap.containsKey(component::class)) {
            addOrReplaceComponentForEntity(component)
        }
    }

    /**
     * Attempts to add the component to the entity.
     * If the component already exists, then throws an ECSComponentAlreadyExistsException.
     */
    fun <T : Any> addComponentOrThrow(component: T) {
        if (componentMap.containsKey(component::class)) {
            throw ECSComponentAlreadyExistsException {
                "Class `${component::class}` of component `$component` already exists for this staging entity."
            }
        } else {
            addOrReplaceComponentForEntity(component)
        }
    }
}