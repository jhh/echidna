package io.j3ff.ecs

internal class EntityManager {

    private val _entities: MutableList<Entity> = mutableListOf()

    val entities: List<Entity>
        get() = _entities

    fun add(entity: Entity) {
        if (_entities.contains(entity)) throw IllegalArgumentException("Entity already added: $entity")
        _entities.add(entity)
    }

    fun addAll(entities: Iterable<Entity>) = entities.forEach { add(it) }


    fun remove(entity: Entity) {
        _entities.remove(entity)
    }
}