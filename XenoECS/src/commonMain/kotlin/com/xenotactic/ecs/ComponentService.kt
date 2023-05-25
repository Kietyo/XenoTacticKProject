package com.xenotactic.ecs

import kotlin.reflect.KClass

class ComponentService(
    val world: World
) {
    val componentTypeToContainerMap = mutableMapOf<KClass<out Any>, ComponentEntityContainer<*>>()
    private val entityIdToActiveComponentKlassSetMap = mutableMapOf<EntityId, MutableSet<KClass<out Any>>>()

    fun getAllComponentsForEntity(entityId: EntityId): List<Any> {
        val activeComponentKlassSet = entityIdToActiveComponentKlassSetMap.getOrElse(entityId) { emptySet() }
        return activeComponentKlassSet.map { componentTypeToContainerMap[it]!!.getComponent(entityId) }
    }

    fun getStatefulEntitySnapshot(entityId: EntityId): StatefulEntity {
        val componentMap = getComponentMap(entityId)
        return StatefulEntity.create(entityId, componentMap)
    }

    fun getStagingEntity(entityId: EntityId): StagingEntity {
        val componentMap = getComponentMap(entityId)
        return StagingEntity(componentMap)
    }

    private fun getComponentMap(entityId: EntityId): MutableMap<KClass<out Any>, Any> {
        val activeComponentKlassSet = entityIdToActiveComponentKlassSetMap.getOrElse(entityId) { emptySet() }
        val componentMap = mutableMapOf<KClass<out Any>, Any>()
        activeComponentKlassSet.forEach {
            val component = componentTypeToContainerMap[it]!!.getComponent(entityId)
            componentMap[it] = component
        }
        return componentMap
    }

    /**
     * Removes the entity by removing all components associated with the entity.
     */
    fun removeEntity(entityId: EntityId) {
        val activeComponentKlasses = entityIdToActiveComponentKlassSetMap.getOrElse(entityId) {
            emptySet()
        }.toSet()
        for (activeComponentKlass in activeComponentKlasses) {
            removeComponentForEntity<Any>(entityId, activeComponentKlass)
        }
    }

    inline fun <reified T> getComponentForEntity(entityId: EntityId): T {
        return getComponentForEntityOrNull(entityId)
            ?: throw ECSComponentNotFoundException {
                "No component type ${T::class} found for entity: ${entityId.id}"
            }
    }

    inline fun <reified T> getComponentForEntityOrNull(entityId: EntityId): T? {
        val arr = componentTypeToContainerMap[T::class]
            ?: return null
        return arr.getComponentOrNull(entityId) as T?
    }

    fun containsComponentForEntity(kClass: KClass<*>, entityId: EntityId): Boolean {
        val arr = componentTypeToContainerMap[kClass]
            ?: return false
        return arr.containsComponent(entityId)
    }

    fun <T : Any> addOrReplaceComponentForEntity(entityId: EntityId, component: T) {
        val container = getOrPutContainer(component::class)
        addOrReplaceComponentInternal(container, entityId, component)
    }

    fun <T : Any> addIfNotExistsForEntity(entityId: EntityId, component: T) {
        val container = getOrPutContainer(component::class)
        if (!container.containsComponent(entityId)) {
            addOrReplaceComponentInternal(container, entityId, component)
        }
    }

    /**
     * Attempts to add the component to the entity.
     * If the component already exists, then throws an ECSComponentAlreadyExistsException.
     */
    fun <T : Any> addComponentOrThrow(entityId: EntityId, component: T) {
        val container = getOrPutContainer(component::class)
        if (container.containsComponent(entityId)) {
            throw ECSComponentAlreadyExistsException {
                "Class `${component::class}` of component `$component` already exists for entity: $entityId"
            }
        } else {
            addOrReplaceComponentInternal(container, entityId, component)
        }
    }

    fun getOrPutContainer(klass: KClass<*>): ComponentEntityContainer<*> {
        return componentTypeToContainerMap.getOrPut(klass) {
            ComponentEntityContainer<Any>(klass, world)
        }
    }

    inline fun <reified T> removeComponentForEntity(entityId: EntityId): T? {
        return removeComponentForEntity(entityId, T::class)
    }

    fun <T : Any> removeComponentForEntity(entityId: EntityId, componentKlass: KClass<*>): T? {
        val container = componentTypeToContainerMap[componentKlass]
            ?: return null
        entityIdToActiveComponentKlassSetMap.getOrPut(entityId) {
            mutableSetOf()
        }.remove(componentKlass)
        return container.removeComponent(entityId) as T?
    }

    inline fun <reified T : Any> addComponentListener(listener: ComponentListener<T>) {
        val container = getOrPutContainer(T::class) as ComponentEntityContainer<T>
        container.addComponentListener(listener)
    }

    inline fun <reified T : Any> addEntityComponentListener(entityId: EntityId, listener: EntityComponentListener<T>): Closeable {
        val container = getOrPutContainer(T::class) as ComponentEntityContainer<T>
        return container.addEntityComponentListener(entityId, listener)
    }

    private fun <T : Any> addOrReplaceComponentInternal(
        container: ComponentEntityContainer<*>,
        entityId: EntityId,
        component: T
    ) {
        container.addOrReplaceComponentInternal(entityId, component)
        entityIdToActiveComponentKlassSetMap.getOrPut(entityId) {
            mutableSetOf()
        }.add(component::class)
    }

}