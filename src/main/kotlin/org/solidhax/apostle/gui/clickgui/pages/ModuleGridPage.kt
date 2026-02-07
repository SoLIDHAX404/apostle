package org.solidhax.apostle.gui.clickgui.pages

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import org.solidhax.apostle.modules.impl.Module
import org.solidhax.apostle.modules.impl.ModuleManager
import org.solidhax.apostle.utils.mouse.cursorX
import org.solidhax.apostle.utils.mouse.cursorY
import org.solidhax.apostle.utils.render.Colors
import org.solidhax.apostle.utils.render.NVGRenderer
import kotlin.math.floor
import kotlin.math.max

class ModuleGridPage(private val onModuleSelected: (Module) -> Unit) : ClickGuiPage {
    private val tileWidth = 140f
    private val tileHeight = 48f
    private val tileGap = 12f
    private val padding = 16f

    override fun render(guiGraphics: GuiGraphics, bounds: UIBounds, mouseX: Int, mouseY: Int, delta: Float) {
        NVGRenderer.text(
            "Modules",
            bounds.x + padding,
            bounds.y + padding,
            18f,
            Colors.WHITE,
            NVGRenderer.defaultFont,
        )

        val modules = ModuleManager.modules
        if (modules.isEmpty()) {
            NVGRenderer.text(
                "No modules available.",
                bounds.x + padding,
                bounds.y + padding + 28f,
                14f,
                Colors.WHITE,
                NVGRenderer.defaultFont,
            )
            return
        }

        val gridStartY = bounds.y + padding + 32f
        val availableWidth = bounds.width - (padding * 2)
        val columns = max(1, floor((availableWidth + tileGap) / (tileWidth + tileGap)).toInt())

        modules.forEachIndexed { index, module ->
            val column = index % columns
            val row = index / columns
            val tileX = bounds.x + padding + column * (tileWidth + tileGap)
            val tileY = gridStartY + row * (tileHeight + tileGap)
            val hover = cursorX >= tileX && cursorX <= tileX + tileWidth && cursorY >= tileY && cursorY <= tileY + tileHeight
            val tileColor = if (hover) Colors.UI_ELEVATED else Colors.UI_SURFACE

            NVGRenderer.filledRect(tileX, tileY, tileWidth, tileHeight, tileColor, 6f)
            NVGRenderer.rect(tileX, tileY, tileWidth, tileHeight, 1f, Colors.UI_BACKGROUND, 6f)
            NVGRenderer.text(
                module.displayName,
                tileX + 12f,
                tileY + 14f,
                14f,
                Colors.WHITE,
                NVGRenderer.defaultFont,
            )
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bounds: UIBounds) {
        if (mouseButtonEvent.button() != 0) return

        val modules = ModuleManager.modules
        if (modules.isEmpty()) return

        val gridStartY = bounds.y + padding + 32f
        val availableWidth = bounds.width - (padding * 2)
        val columns = max(1, floor((availableWidth + tileGap) / (tileWidth + tileGap)).toInt())

        modules.forEachIndexed { index, module ->
            val column = index % columns
            val row = index / columns
            val tileX = bounds.x + padding + column * (tileWidth + tileGap)
            val tileY = gridStartY + row * (tileHeight + tileGap)
            val hit = cursorX >= tileX && cursorX <= tileX + tileWidth && cursorY >= tileY && cursorY <= tileY + tileHeight
            if (hit) {
                onModuleSelected(module)
                return
            }
        }
    }
}
