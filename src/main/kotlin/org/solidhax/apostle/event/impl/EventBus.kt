package org.solidhax.apostle.event.impl

import kotlin.reflect.KClass

object EventBus {
    @PublishedApi
    internal val subscribers: MutableMap<KClass<*>, MutableList<(Any) -> Unit>> = mutableMapOf()

    inline fun <reified T : Any> subscribe(noinline handler: (T) -> Unit) {
        val wrappers = subscribers.getOrPut(T::class) { mutableListOf() }
        wrappers.add { event -> handler(event as T) }
    }

    fun post(event: Any) {
        subscribers[event::class]?.forEach { handler -> handler(event) }
    }
}
inline fun <reified T : Event> subscribe(noinline handler: (T) -> Unit) = EventBus.subscribe(handler)