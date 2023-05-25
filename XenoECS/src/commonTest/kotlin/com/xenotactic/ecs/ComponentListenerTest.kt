package com.xenotactic.ecs

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ComponentListenerTest {

    @Test
    fun addEntityBeforeAddingListenerDoesntTriggerListener() {
        val world = World()

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entityId: EntityId, new: TestComponent) {
                onAddTriggered = true
            }

            override fun onRemove(entityId: EntityId, component: TestComponent) {
                TODO("Not yet implemented")
            }

            override fun onExisting(entityId: EntityId, component: TestComponent) {
            }

        })

        assertFalse(onAddTriggered)
    }

    @Test
    fun addEntityTriggersListener() {
        val world = World()

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entityId: EntityId, new: TestComponent) {
                println("Added component!: $new")
                onAddTriggered = true
            }

        })

        assertFalse(onAddTriggered)

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertTrue(onAddTriggered)
    }

    @Test
    fun addComponentToCreatedEntityResultsInListenerTriggered() {
        val world = World()

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entityId: EntityId, new: TestComponent) {
                println("Added component!: $new")
                onAddTriggered = true
            }
        })

        assertFalse(onAddTriggered)

        val entity = world.addEntity()

        assertFalse(onAddTriggered)

        world.modifyEntity(entity) {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertTrue(onAddTriggered)
    }

    @Test
    fun addIfNotExistsForEntity_doesNotTriggerComponentListenerIfExists() {
        val world = World()

        val entity = world.addEntity() {
            addOrReplaceComponent(TestComponent("test"))
        }

        var onAddTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entityId: EntityId, new: TestComponent) {
                println("Added component!: $new")
                onAddTriggered = true
            }

        })

        assertFalse(onAddTriggered)

        world.modifyEntity(entity) {
            addIfNotExists(TestComponent("test"))
        }

        assertFalse(onAddTriggered)
    }

    @Test
    fun triggersOnExistForComponentListener() {
        val world = World()

        val entity = world.addEntity() {
            addOrReplaceComponent(TestComponent("test"))
        }

        var onExistingTriggered = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onExisting(entityId: EntityId, component: TestComponent) {
                if (component.value == "test") {
                    onExistingTriggered = true
                }
            }

        })

        assertTrue(onExistingTriggered)
    }
}