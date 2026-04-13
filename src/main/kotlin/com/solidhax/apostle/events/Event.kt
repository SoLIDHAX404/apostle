package com.solidhax.apostle.events

import com.solidhax.apostle.Apostle.bus

interface Event {
    fun post(): Boolean {
        bus.post(this)
        return false
    }
}