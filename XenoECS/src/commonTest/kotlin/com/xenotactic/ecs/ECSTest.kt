package com.xenotactic.ecs

import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class ECSTest {

    @Test
    fun entityWithComponentGetsReturned() {
        val world = World()
        val component = TestComponent("test")

        val testComponentContainer = world.getComponentContainer<TestComponent>()

        val entity = world.addEntity {
            addOrReplaceComponent(component)
        }

        assertEquals(testComponentContainer.getComponent(entity), component)
    }

    @Test
    fun sealedClass_regularClassChild() {
        val world = World()

        val sealedClassContainer = world.getComponentContainer<TestSealedClass>()
        val regularClassContainer = world.getComponentContainer<TestSealedClass.RegularClassChild>()

        val component = TestSealedClass.RegularClassChild("blah")

        val entity = world.addEntity {
            addComponentOrThrow(component)
        }

        assertEquals(regularClassContainer.getComponent(entity), component)
        assertEquals(regularClassContainer.getComponent(entity).value, "blah")
        assertFailsWith(ECSComponentNotFoundException::class) {
            sealedClassContainer.getComponent(entity)
        }
    }

    @Test
    fun sealedClass_dataClassChild() {
        val world = World()

        val sealedClassContainer = world.getComponentContainer<TestSealedClass>()
        val dataClassContainer = world.getComponentContainer<TestSealedClass.DataClassChild>()

        val component = TestSealedClass.DataClassChild("blah")

        val entity = world.addEntity {
            addComponentOrThrow(component)
        }

        assertEquals(dataClassContainer.getComponent(entity), component)
        assertEquals(dataClassContainer.getComponent(entity).value, "blah")
        assertFailsWith(ECSComponentNotFoundException::class) {
            sealedClassContainer.getComponent(entity)
        }
    }

    @Test
    fun sealedClass_objectChild() {
        val world = World()

        val sealedClassContainer = world.getComponentContainer<TestSealedClass>()
        val objectClassContainer = world.getComponentContainer<TestSealedClass.ObjectClassChild>()

        val component = TestSealedClass.ObjectClassChild

        val entity = world.addEntity {
            addComponentOrThrow(component)
        }

        assertEquals(objectClassContainer.getComponent(entity), component)
        assertFailsWith(ECSComponentNotFoundException::class) {
            sealedClassContainer.getComponent(entity)
        }
    }

    @Test
    fun enumClass() {
        val world = World()

        val enumClassContainer = world.getComponentContainer<TestEnumClass>()

        val entity = world.addEntity {
            addComponentOrThrow(TestEnumClass.ENUM_1)
        }

        assertEquals(enumClassContainer.getComponent(entity), TestEnumClass.ENUM_1)

        assertFailsWith<ECSComponentAlreadyExistsException> {
            world.modifyEntity(entity) {
                addComponentOrThrow(TestEnumClass.ENUM_2)
            }
        }

        assertEquals(enumClassContainer.getComponent(entity), TestEnumClass.ENUM_1)
    }

    @Test
    fun addIfNotExistsAddsToFamily() {
        val world = World()
        val objectComponentFamily = world.getOrCreateFamily(
            FamilyConfiguration(
                setOf(ObjectComponent::class)
            )
        )

        val newEntity = world.addEntity()

        world.modifyEntity(newEntity) {
            addIfNotExists(ObjectComponent)
        }

        assertEquals(objectComponentFamily.getList().size, 1)
    }

    @Test
    fun addIfNotExistsAddsToFamilyWithListener() {
        val world = World()
        val objectComponentFamily = world.getOrCreateFamily(
            FamilyConfiguration(
                setOf(ObjectComponent::class)
            )
        )
        world.addComponentListener(object : ComponentListener<ObjectComponent> {
            override fun onAddOrReplace(entityId: EntityId, old: ObjectComponent?, new: ObjectComponent) {
                assertEquals(objectComponentFamily.getList().size, 1)
            }
        })

        val newEntity = world.addEntity()

        world.modifyEntity(newEntity) {
            addIfNotExists(ObjectComponent)
        }

        assertEquals(objectComponentFamily.getList().size, 1)
    }

    @Test
    fun removeComponentFromEntity_removesFromFamilyBeforeReachingListener() {
        val world = World()
        val objectComponentFamily = world.getOrCreateFamily(
            FamilyConfiguration(
                setOf(ObjectComponent::class)
            )
        )
        val newEntity = world.addEntity()
        world.modifyEntity(newEntity) {
            addIfNotExists(ObjectComponent)
        }

        world.addComponentListener(object : ComponentListener<ObjectComponent> {
            override fun onRemove(entityId: EntityId, component: ObjectComponent) {
                assertEquals(objectComponentFamily.getList().size, 0)
            }
        })

        world.modifyEntity(newEntity) {
            removeComponent<ObjectComponent>()
        }

        assertEquals(objectComponentFamily.getList().size, 0)
    }

    @Test
    fun removeEntityFromWorld() {
        val world = World()

        val entity = world.addEntity()

        assertTrue(world.containsEntity(entity))

        world.modifyEntity(entity) {
            removeThisEntity()
        }

        assertFalse(world.containsEntity(entity))
    }

    @Test
    fun removeEntityWithComponentsFromWorld() {
        val world = World()

        var onAddCalled = false
        var onRemoveCalled = false
        var onExistingCalled = false

        world.addComponentListener(object : ComponentListener<TestComponent> {
            override fun onAdd(entityId: EntityId, new: TestComponent) {
                onAddCalled = true
            }

            override fun onRemove(entityId: EntityId, component: TestComponent) {
                onRemoveCalled = true
            }

            override fun onExisting(entityId: EntityId, component: TestComponent) {
                onExistingCalled = true
            }

        })

        val testComponentContainer = world.getComponentContainer<TestComponent>()

        val entity = world.addEntity {
            addComponentOrThrow(TestComponent("blah"))
        }

        assertTrue(onAddCalled)
        assertFalse(onRemoveCalled)
        assertFalse(onExistingCalled)
        assertTrue(world.containsEntity(entity))
        assertTrue(testComponentContainer.containsComponent(entity))

        world.modifyEntity(entity) {
            removeThisEntity()
        }

        assertTrue(onRemoveCalled)
        assertFalse(world.containsEntity(entity))
        assertFalse(testComponentContainer.containsComponent(entity))
    }

    @Test
    fun removeEntityWhenWorldContainsMultipleEntities() {
        val world = World().also { world ->
            world.addSystem(object : System() {
                override val familyConfiguration: FamilyConfiguration =
                    FamilyConfiguration(allOfComponents = setOf(TestComponent::class))

                override fun update(deltaTime: Duration) {
                    getFamily().getNewList().forEach {
                        world.modifyEntity(it) {
                            removeThisEntity()
                        }
                    }
                }
            })
        }

        val e1 = world.addEntity() {
            addComponentOrThrow(TestComponent("entity1"))
            addComponentOrThrow(ObjectComponent)
        }
        val e2 = world.addEntity() {
            addComponentOrThrow(TestComponent("entity2"))
            addComponentOrThrow(ObjectComponent)
        }

        world.update(1.seconds)

        assertEquals(world.numEntities, 0)
    }

    @Test
    fun pendingModificationsGetCleared() {
        val world = World()

        world.addSystem(object : System() {
            override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
                allOfComponents = setOf(TestComponent::class),
                noneOfComponents = setOf(TestComponent2::class)
            )

            override fun update(deltaTime: Duration) {
                getFamily().getNewList().forEach {
                    world.modifyEntity(it) {
                        addComponentOrThrow(TestComponent2("2"))
                    }
                }
            }

        })

        world.addEntity {
            addComponentOrThrow(TestComponent("1"))
        }

        world.update(1.seconds)
        assertTrue(world.pendingModifications.isEmpty())

        world.update(1.seconds)
        assertTrue(world.pendingModifications.isEmpty())
    }
}