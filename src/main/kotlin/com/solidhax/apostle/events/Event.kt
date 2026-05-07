package com.solidhax.apostle.events

import com.solidhax.apostle.Apostle.bus
import com.solidhax.apostle.utils.errorMessage

interface Event {
    fun post(): Boolean {
        runCatching {
            bus.post(this)
        }.onFailure {
            errorMessage(it, this)
        }

        return false
    }
}