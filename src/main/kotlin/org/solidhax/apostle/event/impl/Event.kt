package org.solidhax.apostle.event.impl

abstract class Event {

    open fun postAndCatch(): Boolean {
        EventBus.post(this)
        return false
    }
}