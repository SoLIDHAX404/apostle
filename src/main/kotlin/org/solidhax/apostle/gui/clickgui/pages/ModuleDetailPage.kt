package org.solidhax.apostle.gui.clickgui.pages

import net.minecraft.client.gui.GuiGraphics
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.render.NVGRenderer

class ModuleDetailPage(private val module: Module) : ClickGuiPage {
    override fun render(guiGraphics: GuiGraphics, bounds: UIBounds, mouseX: Int, mouseY: Int, delta: Float) {
        NVGRenderer.text(
            module.displayName,
            bounds.x + 16f,
            bounds.y + 16f,
            20f,
            Colors.WHITE,
            NVGRenderer.defaultFont,
        )
    }
}
