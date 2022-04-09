package io.j3ff.ecs

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class EntityTest {
    private val entity = Engine().createEntity()
    private val foo = StringComponent("foo")
    private val bar = StringComponent("bar")
    private val nc = NumberComponent(2767)

    @Test
    fun `add a Component`() {
        entity.add(foo)
        assertEquals(1, entity.components.size)
        var actual = entity.get<StringComponent>()
        assertSame(foo, actual)

        entity.add(bar)
        assertEquals(1, entity.components.size)
        actual = entity.get()
        assertSame(bar, actual)

    }

    @Test
    fun `get a Component with type`() {
        assertThrows<NoSuchElementException> { entity.get<StringComponent>() }

        entity.add(foo).add(nc)
        val actual = entity.get<StringComponent>()
        assertSame(foo, actual)
        assertNotSame(bar, actual)
    }

    @Test
    fun `get a Component with class`() {
        assertThrows<NoSuchElementException> { entity.get(StringComponent::class) }
        entity.add(foo).add(nc)
        val actual = entity.get(NumberComponent::class)
        assertSame(nc, actual)
    }

    @Test
    fun `remove a Component`() {
        entity.add(foo)
        val added = entity.get<StringComponent>()
        assertSame(foo, added)

        val deleted = entity.remove<StringComponent>()
        assertSame(foo, deleted)
        assertThrows<NoSuchElementException> { entity.get<StringComponent>() }
    }

    @Test
    fun `get family`() {
        entity.add(foo).add(nc)
        assertEquals(setOf(foo::class, nc::class), entity.family)
    }
}