package org.solidhax.apostle

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry
import org.solidhax.apostle.utils.render.NVGRenderer
import org.solidhax.apostle.utils.render.NVGSpecialRenderer

class Apostle : ClientModInitializer {
    override fun onInitializeClient() {
        SpecialGuiElementRegistry.register { context -> NVGSpecialRenderer(context.vertexConsumers()) }

        HudRenderCallback.EVENT.register { graphics, tracker ->
            NVGSpecialRenderer.draw(graphics, 0, 0, graphics.guiWidth(), graphics.guiHeight()) {
                NVGRenderer.rect(10f, 10f, 100f, 100f, 0, 5f)
            }
        }
    }
}
