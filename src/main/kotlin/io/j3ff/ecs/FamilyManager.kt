package io.j3ff.ecs

import kotlin.reflect.KClass

typealias Family = Set<KClass<out Component>>

internal class FamilyManager(private val entities: List<Entity>) {

    private val entitiesByFamily = mutableMapOf<Family, MutableList<Entity>>()

    fun entitiesFor(family: Family): List<Entity> {
        if (!entitiesByFamily.containsKey(family)) {
            entitiesByFamily[family] = entities.filter { it.family.containsAll(family) }.toMutableList()
        }
        entities.forEach { updateFamilyMembership(it) }
        return entitiesByFamily[family]!!
    }

    fun updateFamilyMembership(entity: Entity) {
        if (entity !in entities) {
            entitiesByFamily.values.forEach { it.remove(entity) }
            return
        }

        val family = entity.family
        entitiesByFamily.forEach {
            if (family.containsAll(it.key)) it.value.addIfAbsent(entity)
            else it.value.remove(entity)
        }
    }
}

private fun MutableList<Entity>.addIfAbsent(entity: Entity) {
    if (entity !in this) this.add(entity)
}