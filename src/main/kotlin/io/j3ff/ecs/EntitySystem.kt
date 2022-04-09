package io.j3ff.ecs


abstract class EntitySystem(val priority: Int, var isRunning: Boolean = true) {

    abstract fun addedToEngine(engine: Engine)

    open fun removedFromEngine(engine: Engine) {}

    abstract fun update(deltaTime: Double)

}