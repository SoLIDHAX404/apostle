package org.solidhax.apostle.event

import org.solidhax.apostle.event.impl.Event

abstract class ClientEvent : Event() {
    class Start : ClientEvent()
    class Stop : ClientEvent()
}