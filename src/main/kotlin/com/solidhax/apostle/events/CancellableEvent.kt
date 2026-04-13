package com.solidhax.apostle.events

import com.solidhax.apostle.Apostle.bus

abstract class CancellableEvent : Event {
    var cancelled = false
        private set

    fun cancel() {
        cancelled = true
    }

    override fun post(): Boolean {
        bus.post(this)
        return cancelled
    }
}