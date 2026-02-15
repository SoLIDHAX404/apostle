package org.solidhax.apostle.event

import net.fabricmc.fabric.api.client.rendering.v1.world.AbstractWorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import org.solidhax.apostle.event.impl.Event
import org.solidhax.apostle.utils.render.RenderConsumer

abstract class RenderEvent(open val context: AbstractWorldRenderContext) : Event() {
    class Extract(override val context: WorldExtractionContext, val consumer: RenderConsumer) : RenderEvent(context)
    class Last(override val context: WorldRenderContext) : RenderEvent(context)
}