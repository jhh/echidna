package io.j3ff.ecs

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class EngineTest {
    private val engine = Engine()
    private val foo = StringComponent("foo")
    private val bar = StringComponent("bar")
    private val n27 = NumberComponent(27)
    private val n67 = NumberComponent(67)
    private val e1 = engine.createEntity().add(foo).add(n27)
    private val e2 = engine.createEntity().add(bar)
    private val e3 = engine.createEntity().add(n27)
    private val e4 = engine.createEntity().add(bar).add(n67)


    @Nested
    inner class `updating Entity List` {
        val entityList = engine.entitiesWith(StringComponent::class, NumberComponent::class)

        @Test
        fun `get updating list of Entity with required Components`() {
            val e5 = engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42))
            val e6 =
                engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42)).add(BooleanComponent(true))
            assertContentsEqualInAnyOrder(listOf(e1, e4, e5, e6), entityList)
        }

        @Test
        fun `adding Entity adds it to updating list`() {
            val e5 = engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42))
            val e6 =
                engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42)).add(BooleanComponent(true))
            assertContentsEqualInAnyOrder(listOf(e1, e4, e5, e6), entityList)
        }

        @Test
        fun `removing Entity removes it from updating list`() {
            val e5 = engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42))
            engine.removeEntity(e1)
            assertContentsEqualInAnyOrder(listOf(e4, e5), entityList)
        }

        @Test
        fun `adding Entity with superset of Family succeeds`() {
            val e5 = engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42))
            val e6 =
                engine.createEntity().add(StringComponent("baz")).add(NumberComponent(42)).add(BooleanComponent(true))
            assertContentsEqualInAnyOrder(listOf(e1, e4, e5, e6), entityList)
        }

        @Test
        fun `adding Entity with subset of Family fails`() {
            engine.createEntity().add(NumberComponent(42)).add(BooleanComponent(true))
            assertContentsEqualInAnyOrder(listOf(e1, e4), entityList)
        }
    }

    @Nested
    inner class `modify Entity Component membership`() {
        val stringFamily = engine.entitiesWith(StringComponent::class)
        val numberFamily = engine.entitiesWith(NumberComponent::class)
        val bothFamily = engine.entitiesWith(StringComponent::class, NumberComponent::class)

        @Test
        fun `adding component to Entity updates list`() {
            assertContentsEqualInAnyOrder(listOf(e1, e2, e4), stringFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e3, e4), numberFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e4), bothFamily)

            val yolo = StringComponent("yolo")
            e3.add(yolo) // e3 now also has String, Number
            assertContentsEqualInAnyOrder(listOf(e1, e3, e4), bothFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e2, e3, e4), stringFamily)

            val team = NumberComponent(2767)
            e2.add(team) // e2 now also has String, Number
            assertContentsEqualInAnyOrder(listOf(e1, e2, e3, e4), bothFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e2, e3, e4), stringFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e2, e3, e4), numberFamily)
        }

        @Test
        fun `Removing component from Entity updates list`() {
            assertContentsEqualInAnyOrder(listOf(e1, e2, e4), stringFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e3, e4), numberFamily)
            assertContentsEqualInAnyOrder(listOf(e1, e4), bothFamily)

            e1.remove<StringComponent>()
            assertContentsEqualInAnyOrder(listOf(e4), bothFamily)
            assertContentsEqualInAnyOrder(listOf(e2, e4), stringFamily)
        }
    }

    @Nested
    inner class EntitySystems {

        @Test
        fun `adding EntitySystem succeeds`() {
            val m: EntitySystem = MovementSystem()
            engine.addSystem(m)
            val p = PositionSystem()
            engine.addSystem(p)
            assertContentsEqualInAnyOrder(listOf(m, p), engine.getSystems())
        }

        @Test
        fun `adding EntitySystem removes previous with same type`() {
            val m1 = MovementSystem()
            engine.addSystem(m1)
            assertTrue(m1.addedToEngineCalled)
            assertContentsEqualInAnyOrder(listOf(m1), engine.getSystems())

            val m2: EntitySystem = MovementSystem()
            engine.addSystem(m2)
            assertTrue(m1.removedFromEngineCalled)
            assertContentsEqualInAnyOrder(listOf(m2), engine.getSystems())
        }

        @Test
        fun `getting EntitySystem by class`() {
            val m1 = MovementSystem()
            engine.addSystem(m1)
            val p1 = PositionSystem()
            engine.addSystem(p1)

            val m2 = engine.getSystem(MovementSystem::class)
            assertSame(m1, m2)

            val p2 = engine.getSystem(PositionSystem::class)
            assertSame(p1, p2)
        }
    }

}

