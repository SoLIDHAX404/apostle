package org.solidhax.apostle.event.impl

abstract class Event {
    open fun post(): Boolean {
        EventBus.post(this)

        return false
    }
}