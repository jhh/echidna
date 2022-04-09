package io.j3ff.ecs

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

internal class EntityManagerTest {
    private val entityManager = EntityManager()
    private val engine = Engine()

    @Test
    fun `adding Entity twice throws exception`() {
        val e = engine.createEntity()
        entityManager.add(e)
        assertThrows<IllegalArgumentException> {
            entityManager.add(e)
        }
    }

    @Test
    fun `get all Entity instances`() {
        val e1 = engine.createEntity()
        val e2 = engine.createEntity()
        entityManager.add(e1)
        entityManager.add(e2)
        assertContentsEqualInAnyOrder(listOf(e1, e2), entityManager.entities)
    }

    @Test
    fun `remove an Entity`() {
        val e = engine.createEntity()
        entityManager.add(e)
        assertContentsEqualInAnyOrder(listOf(e), entityManager.entities)
        entityManager.remove(e)
        assertContentsEqualInAnyOrder(emptyList(), entityManager.entities)
    }

}