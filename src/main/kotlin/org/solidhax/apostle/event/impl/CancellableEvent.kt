package org.solidhax.apostle.event.impl

abstract class CancellableEvent : Event() {
    var isCancelled = false
        private set

    fun cancel() {
        isCancelled = true
    }

    override fun postAndCatch(): Boolean {
        EventBus.post(this)

        return isCancelled
    }
}