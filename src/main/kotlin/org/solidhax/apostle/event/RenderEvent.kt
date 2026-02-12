package org.solidhax.apostle.event

import net.fabricmc.fabric.api.client.rendering.v1.world.AbstractWorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import org.solidhax.apostle.event.impl.Event

abstract class RenderEvent(open val context: AbstractWorldRenderContext) : Event() {
    class Extract(override val context: WorldExtractionContext) : RenderEvent(context)
    class Last(override val context: WorldRenderContext) : RenderEvent(context)
}