package org.solidhax.apostle.event

import org.solidhax.apostle.event.impl.Event

abstract class WorldEvent : Event() {
    class Load : WorldEvent()
    class Unload : WorldEvent()
}