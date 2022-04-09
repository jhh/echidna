package io.j3ff.ecs

import kotlin.reflect.KClass

class Entity(private val engine: Engine) {

    private val _components: MutableSet<Component> = mutableSetOf()

    val components: Set<Component>
        get() = _components

    val family: Family
        get() = _components.map { it::class }.toSet()

    fun add(component: Component): Entity {
        val removed = _components.removeAll { it::class == component::class }
        _components.add(component)
        if (!removed) engine.updateFamilyMembership(this)
        return this
    }

    inline fun <reified C : Component> get(): C {
        return get(C::class)
    }

    inline fun <reified C : Component> remove(): C {
        return remove(C::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <C : Component> get(componentClass: KClass<C>): C {
        val component = _components.find { it::class == componentClass } ?: throw NoSuchElementException()
        return component as C
    }

    fun <C : Component> remove(componentClass: KClass<C>): C {
        val component = get(componentClass)
        _components.remove(component)
        engine.updateFamilyMembership(this)
        return component
    }

    override fun toString(): String {
        return "Entity(components=$_components)"
    }

}