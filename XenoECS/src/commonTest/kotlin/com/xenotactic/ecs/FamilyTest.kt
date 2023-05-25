package com.xenotactic.ecs

import com.kietyo.ktruth.assertThat
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FamilyTest {

    @Test
    fun createFamilyFirst_addingEntityAfterwardsUpdatesFamily() {
        val world = World()

        val family = world.getOrCreateFamily(
            FamilyConfiguration(
                allOfComponents = setOf(TestComponent::class)
            )
        )

        assertEquals(family.getList().size, 0)

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertEquals(family.getList().size, 1)
    }

    @Test
    fun familyWithObject() {
        val world = World()
        val family = world.getOrCreateFamily(
            FamilyConfiguration(
                allOfComponents = setOf(ObjectComponent::class)
            )
        )

        assertEquals(family.getList().size, 0)

        val entity = world.addEntity {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)
    }

    @Test
    fun familyWithObjectAndComponent() {
        val world = World()
        val family = world.getOrCreateFamily(
            FamilyConfiguration(
                allOfComponents = setOf(TestComponent::class, ObjectComponent::class)
            )
        )

        assertEquals(family.getList().size, 0)

        val entity = world.addEntity {
            addOrReplaceComponent(TestComponent("test"))
        }

        assertEquals(family.getList().size, 0)

        world.modifyEntity(entity) {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)
    }

    @Test
    fun familyWithOneComponent_removingComponentUpdatesFamily() {
        val world = World()

        val family = world.getOrCreateFamily(
            FamilyConfiguration(
            allOfComponents = setOf(ObjectComponent::class)
        )
        )

        val entity = world.addEntity {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)

        world.modifyEntity(entity) {
            removeComponent<ObjectComponent>()
        }

        assertEquals(family.getList().size, 0)
    }

    @Test
    fun replacingComponentForEntityShouldntChangeFamily() {
        val world = World()

        val family = world.getOrCreateFamily(
            FamilyConfiguration(
            allOfComponents = setOf(ObjectComponent::class)
        )
        )

        val entity = world.addEntity {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)

        world.modifyEntity(entity) {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)
    }

    @Test
    fun addIfNotExistsComponentForEntityShouldntChangeFamily() {
        val world = World()

        val family = world.getOrCreateFamily(
            FamilyConfiguration(
                allOfComponents = setOf(ObjectComponent::class)
            )
        )

        val entity = world.addEntity {
            addOrReplaceComponent(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)

        world.modifyEntity(entity) {
            addIfNotExists(ObjectComponent)
        }

        assertEquals(family.getList().size, 1)
    }

    @Test
    fun gettingSameFamilyTwice_doesNotIncreaseFamilySize() {
        val world = World()

        val entity = world.addEntity {
            addOrReplaceComponent(ObjectComponent)
        }

        val family1 = world.getOrCreateFamily(FamilyConfiguration.allOf(ObjectComponent::class))

        assertThat(family1.size).isEqualTo(1)

        val family2 = world.getOrCreateFamily(FamilyConfiguration.allOf(ObjectComponent::class))
        assertThat(family1.size).isEqualTo(1)
        assertThat(family2.size).isEqualTo(1)
    }
}