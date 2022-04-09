package io.j3ff.ecs

import kotlin.reflect.KClass

class Engine {

    private val entityManager = EntityManager()
    private val familyManager = FamilyManager(entityManager.entities)
    private val systems = mutableListOf<EntitySystem>()

    fun createEntity(): Entity = Entity(this).also {
        entityManager.add(it)
    }

    fun removeEntity(entity: Entity) {
        entityManager.remove(entity)
        familyManager.updateFamilyMembership(entity)
    }

    fun entitiesWith(vararg componentClasses: KClass<out Component>): List<Entity> =
        familyManager.entitiesFor(setOf(*componentClasses))

    internal fun updateFamilyMembership(entity: Entity) = familyManager.updateFamilyMembership(entity)

    fun addSystem(system: EntitySystem) {
        val oldSystem = systems.find { it::class == system::class }
        if (oldSystem != null) {
            systems.remove(oldSystem)
            oldSystem.removedFromEngine(this)
        }

        systems.add(system)
        system.addedToEngine(this)
        systems.sortBy { it.priority }
    }

    fun getSystems(): List<EntitySystem> = systems

    @Suppress("UNCHECKED_CAST")
    fun <T : EntitySystem> getSystem(systemClass: KClass<out T>): T = systems.find { it::class == systemClass } as T


    fun update(deltaTime: Double) {
        for (system in systems) system.update(deltaTime)
    }
}
