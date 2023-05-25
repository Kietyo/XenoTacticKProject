package com.xenotactic.ecs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class FamilyListenerTest {
    class TestListener(
        world: World
    ) : FamilyListener {
        override val familyConfiguration: FamilyConfiguration
                = FamilyConfiguration(allOfComponents = setOf(TestComponent::class))
        val testComponentContainer = world.getComponentContainer<TestComponent>()
        val existingComponentValues = mutableSetOf<String>()
        val addedComponentValues = mutableSetOf<String>()
        var numRemove = 0

        override fun onAdd(entityId: EntityId) {
            val component = testComponentContainer.getComponent(entityId)
            addedComponentValues.add(component.value)
        }

        override fun onRemove(entityId: EntityId) {
            numRemove++
        }

        override fun onExisting(entityId: EntityId) {
            val component = testComponentContainer.getComponent(entityId)
            existingComponentValues.add(component.value)
        }
    }

    @Test
    fun listener() {
        val world = World().apply {
            addEntity {
                addComponentOrThrow(TestComponent("initial test"))
            }
        }

        val listener = TestListener(
            world
        )

        world.addFamilyListener(listener)

        val entity = world.addEntity {
            addComponentOrThrow(TestComponent("test"))
        }

        assertContentEquals(listener.existingComponentValues, setOf("initial test").asIterable())
        assertContentEquals(listener.addedComponentValues, setOf("test").asIterable())
        assertEquals(listener.numRemove, 0)

        world.modifyEntity(entity) {
            removeComponent<TestComponent>()
        }

        assertEquals(listener.numRemove, 1)
    }
}