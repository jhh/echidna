package io.j3ff.ecs

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun <T> assertContentsEqualInAnyOrder(expected: List<T>, actual: List<T>) {
    assertEquals(expected.size, actual.size, "expected and actual don't have same length")
    assertTrue("expected doesn't contain all actual") { expected.containsAll(actual) }
    assertTrue("actual doesn't contain all expected") { actual.containsAll(expected) }
}

data class StringComponent(val name: String) : Component
data class NumberComponent(val id: Int) : Component
data class BooleanComponent(val enabled: Boolean) : Component

class MovementSystem : EntitySystem(0) {
    var addedToEngineCalled = false
    var removedFromEngineCalled = false

    override fun addedToEngine(engine: Engine) {
        addedToEngineCalled = true
    }

    override fun removedFromEngine(engine: Engine) {
        removedFromEngineCalled = true
    }

    override fun update(deltaTime: Double) {
        TODO("Not yet implemented")
    }
}

class PositionSystem : EntitySystem(10) {
    var addedToEngineCalled = false
    var removedFromEngineCalled = false

    override fun addedToEngine(engine: Engine) {
        addedToEngineCalled = true
    }

    override fun removedFromEngine(engine: Engine) {
        removedFromEngineCalled = true
    }

    override fun update(deltaTime: Double) {
        TODO("Not yet implemented")
    }
}
