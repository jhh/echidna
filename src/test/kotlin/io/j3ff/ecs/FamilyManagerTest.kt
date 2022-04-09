@file:Suppress("MemberVisibilityCanBePrivate")

package io.j3ff.ecs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

internal class FamilyManagerTest {
    val engine = Engine()
    val foo = StringComponent("foo")
    val bar = StringComponent("bar")
    val n27 = NumberComponent(27)
    val n67 = NumberComponent(67)

    val e1 = engine.createEntity().add(foo).add(n27)
    val e3 = engine.createEntity().add(n27)
    val e2 = engine.createEntity().add(bar)
    val e4 = engine.createEntity().add(bar).add(n67)

    val entityManager = EntityManager().apply {
        addAll(listOf(e1, e2, e3, e4))
    }

    @Test
    fun `get entities by Family`() {
        val familyManager = FamilyManager(entityManager.entities)
        var entities = familyManager.entitiesFor(setOf(StringComponent::class))
        assertContentsEqualInAnyOrder(listOf(e1, e2, e4), entities)

        entities = familyManager.entitiesFor(setOf(NumberComponent::class))
        assertContentsEqualInAnyOrder(listOf(e1, e3, e4), entities)

        entities = familyManager.entitiesFor(setOf(StringComponent::class, NumberComponent::class))
        assertContentsEqualInAnyOrder(listOf(e1, e4), entities)

        val sameEntities = familyManager.entitiesFor(setOf(StringComponent::class, NumberComponent::class))
        assertSame(entities, sameEntities)
    }

    @Test
    fun `adding or removing a component updates Family membership`() {
        val familyManager = FamilyManager(entityManager.entities)
        val entitiesBoth = familyManager.entitiesFor(setOf(StringComponent::class, NumberComponent::class))
        val entitiesString = familyManager.entitiesFor(setOf(StringComponent::class))
        val entitiesNumber = familyManager.entitiesFor(setOf(NumberComponent::class))
        val entitiesBoolean = familyManager.entitiesFor(setOf(BooleanComponent::class))

        assertContentsEqualInAnyOrder(listOf(e1, e4), entitiesBoth)
        assertContentsEqualInAnyOrder(listOf(e1, e2, e4), entitiesString)
        assertContentsEqualInAnyOrder(listOf(e1, e3, e4), entitiesNumber)
        assertEquals(emptyList(), entitiesBoolean)

        // adding a Component
        e2.add(n67)
        assertEquals(setOf(StringComponent::class, NumberComponent::class), e2.family)
        familyManager.updateFamilyMembership(e2)
        assertContentsEqualInAnyOrder(listOf(e1, e2, e4), entitiesBoth)

        e2.add(BooleanComponent(true))
        familyManager.updateFamilyMembership(e2)
        assertContentsEqualInAnyOrder(listOf(e1, e2, e4), entitiesBoth)
        assertContentsEqualInAnyOrder(listOf(e2), entitiesBoolean)

        // removing a Component
        e1.remove(NumberComponent::class)
        familyManager.updateFamilyMembership(e1)
        assertContentsEqualInAnyOrder(listOf(e2, e4), entitiesBoth)
    }

    @Test
    fun `adding or removing an entity updates Family membership`() {
        val familyManager = FamilyManager(entityManager.entities)
        val entities = familyManager.entitiesFor(setOf(StringComponent::class))
        assertContentsEqualInAnyOrder(listOf(e1, e2, e4), entities)

        val entity = engine.createEntity()
            .add(StringComponent("entity"))
            .add(NumberComponent(5))
            .also {
                entityManager.add(it)
                familyManager.updateFamilyMembership(it)
            }
        assertContentsEqualInAnyOrder(listOf(entity, e1, e2, e4), entities)

        entityManager.remove(e2)
        familyManager.updateFamilyMembership(e2)
        assertContentsEqualInAnyOrder(listOf(entity, e1, e4), entities)
    }
}