package com.xenotactic.ecs

interface ComponentListener<T> {
    fun onAdd(entityId: EntityId, new: T) = Unit
    fun onReplace(entityId: EntityId, old: T, new: T) = Unit

    // Listener for when a component gets added to the entity.
    fun onAddOrReplace(entityId: EntityId, old: T?, new: T) = Unit

    // Listener for when a component gets removed from an entity.
    fun onRemove(entityId: EntityId, component: T) = Unit

    // Listener for entities that already contain the component.
    fun onExisting(entityId: EntityId, component: T) = Unit
}