package org.solidhax.apostle.event.impl

abstract class CancellableEvent : Event() {
    var cancelled = false
    private set

    fun cancel() {
        cancelled = true
    }

    override fun post(): Boolean {
        EventBus.post(this)

        return cancelled
    }
}