package org.solidhax.apostle

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import org.solidhax.apostle.event.KeyPressEvent
import org.solidhax.apostle.event.impl.subscribe
import org.solidhax.apostle.utils.render.NVGRenderer
import org.solidhax.apostle.utils.render.NVGSpecialRenderer

class Apostle : ClientModInitializer {
    override fun onInitializeClient() {
        SpecialGuiElementRegistry.register { context -> NVGSpecialRenderer(context.vertexConsumers()) }
    }
}
