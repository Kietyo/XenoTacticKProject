package com.xenotactic.ecs

import kotlin.reflect.KClass

internal expect fun FamilyConfiguration.getString(): String

data class FamilyConfiguration(
    // A component must have all of these components to be a part of the family.
    val allOfComponents: Set<KClass<*>> = emptySet(),
    // A component can have any of these components to be a part of the family.
    val anyOfComponents: Set<KClass<*>> = emptySet(),
    // A component must not have any of these components to be a part of the family.
    val noneOfComponents: Set<KClass<*>> = emptySet()
) {
    companion object {
        val EMPTY = FamilyConfiguration()
        fun allOf(vararg components: KClass<*>): FamilyConfiguration {
            return FamilyConfiguration(allOfComponents = components.toSet())
        }
    }

    override fun toString(): String {
        return getString()
    }
}

data class Family(
    private var entities: ArrayList<EntityId>
) {
    val isEmpty get() = entities.isEmpty()
    val size get() = entities.size
    fun first() = entities.first()
    fun getSequence(): Sequence<EntityId> = entities.asSequence()
    fun getList(): List<EntityId> = entities

    // Useful to avoid concurrent modifications
    fun getNewList(): List<EntityId> = entities.toList()

    // Only adds the entity to the family if it doesn't yet exist.
    // If the entity already exists, then we won't add it to this family again.
    internal fun addEntityIfNotExists(entityId: EntityId) {
        entities.add(entityId)
    }

    internal fun removeEntity(entityId: EntityId) {
        entities.remove(entityId)
    }

    fun containsEntity(entityId: EntityId): Boolean {
        return entities.contains(entityId)
    }

    companion object {
        val EMPTY = Family(ArrayList())
    }
}

data class FamilyNode(
    val family: Family,
    // Listeners for this family
    val listeners: MutableList<FamilyListener> = mutableListOf()
)

class FamilyService(
    val world: World,
    val componentService: ComponentService
) {
    internal val families = mutableMapOf<FamilyConfiguration, FamilyNode>()

    fun updateFamiliesForEntity(entityId: EntityId) {
        for ((config, node) in families) {
            if (matchesFamilyConfiguration(entityId, config)) {
                val familyAlreadyContainsEntity = node.family.containsEntity(entityId)
                if (!familyAlreadyContainsEntity) {
                    // Only add and call listeners if it didn't already exists in the family.
                    node.family.addEntityIfNotExists(entityId)
                    for (listener in node.listeners) {
                        listener.onAdd(entityId)
                    }
                }
            } else {
                if (node.family.containsEntity(entityId)) {
                    // Only remove entity and call listeners if the entity was already a part
                    // of the family.
                    node.family.removeEntity(entityId)
                    for (listener in node.listeners) {
                        listener.onRemove(entityId)
                    }
                }
            }
        }
    }

    internal fun matchesFamilyConfiguration(entityId: EntityId, familyConfiguration: FamilyConfiguration): Boolean {
        return familyConfiguration.allOfComponents.all {
            componentService.containsComponentForEntity(it, entityId)
        } && (familyConfiguration.anyOfComponents.isEmpty() || familyConfiguration.anyOfComponents.any {
            componentService.containsComponentForEntity(it, entityId)
        }) && familyConfiguration.noneOfComponents.none {
            componentService.containsComponentForEntity(it, entityId)
        }
    }

    fun createFamilyIfNotExistsAndAddListener(
        listener: FamilyListener
    ) {
        val node = getOrCreateFamily(listener.familyConfiguration)
        node.listeners.add(listener)
        for (entity in node.family.getSequence()) {
            listener.onExisting(entity)
        }
    }

    fun getOrCreateFamily(familyConfiguration: FamilyConfiguration): FamilyNode {
        return families.getOrPut(familyConfiguration) {
            val newNode = FamilyNode(
                Family(
                    ArrayList()
                )
            )

            // New node. Need to initialize.
            world.entities.asSequence().filter {
                matchesFamilyConfiguration(it, familyConfiguration)
            }.forEach {
                newNode.family.addEntityIfNotExists(it)
            }

            newNode
        }
    }
}