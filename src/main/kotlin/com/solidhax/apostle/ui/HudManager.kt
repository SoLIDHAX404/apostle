package com.solidhax.apostle.ui

import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.mixin.invoker.GuiInvoker
import com.solidhax.apostle.modules.internal.ModuleManager
import com.solidhax.apostle.ui.setting.HudAlignment
import com.solidhax.apostle.ui.setting.HudSetting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.max
import kotlin.math.roundToInt

object HudManager : Screen(Component.literal("HUD Manager")) {

    private const val GRID_SIZE = 8
    private const val MIN_SCALE = 0.5f
    private const val MAX_SCALE = 4.0f
    private const val SCALE_STEP = 0.1f

    private var activeSetting: HudSetting? = null
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTracker: Float) {
        renderBackdrop(guiGraphics)
        renderCrosshair(guiGraphics)
        renderHudElements(guiGraphics, mouseX.toFloat(), mouseY.toFloat())
        super.render(guiGraphics, mouseX, mouseY, deltaTracker)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) = Unit

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        findHoveredSetting(event.x().toFloat(), event.y().toFloat())?.let { setting ->
            if (event.button() == 1) {
                val currentLeft = resolvedLeft(setting)
                setting.value.alignment = setting.value.alignment.next()
                setting.value.x = xForAlignment(setting, currentLeft)
                clampToScreen(setting, snapToGrid = false)
                return true
            }

            if (event.button() == 0) {
                activeSetting = setting
                dragOffsetX = event.x().toFloat() - resolvedLeft(setting)
                dragOffsetY = event.y().toFloat() - setting.value.y
                return true
            }
        }

        activeSetting = null
        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        if (event.button() != 0) {
            return super.mouseDragged(event, dragX, dragY)
        }

        activeSetting?.let { setting ->
            val snappedLeft = snap((event.x().toFloat() - dragOffsetX).toInt())
            setting.value.x = xForAlignment(setting, snappedLeft)
            setting.value.y = snap((event.y().toFloat() - dragOffsetY).toInt())
            clampToScreen(setting)
            return true
        }

        return super.mouseDragged(event, dragX, dragY)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (event.button() == 0) {
            activeSetting = null
            return true
        }

        return super.mouseReleased(event)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        val setting = findHoveredSetting(mouseX.toFloat(), mouseY.toFloat()) ?: activeSetting
        if (setting != null && verticalAmount != 0.0) {
            setting.value.scale = (setting.value.scale - (verticalAmount.toFloat() * SCALE_STEP)).coerceIn(MIN_SCALE, MAX_SCALE)
            clampToScreen(setting)
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    private fun renderBackdrop(guiGraphics: GuiGraphics) {
        guiGraphics.fill(0, 0, width, height, 0xB0081018.toInt())

        var x = 0
        while (x <= width) {
            guiGraphics.fill(x, 0, x + 1, height, if (x % (GRID_SIZE * 4) == 0) 0x1AFFFFFF else 0x0DFFFFFF)
            x += GRID_SIZE
        }

        var y = 0
        while (y <= height) {
            guiGraphics.fill(0, y, width, y + 1, if (y % (GRID_SIZE * 4) == 0) 0x1AFFFFFF else 0x0DFFFFFF)
            y += GRID_SIZE
        }
    }

    private fun renderHudElements(guiGraphics: GuiGraphics, mouseX: Float, mouseY: Float) {
        ModuleManager.hudSettings.forEach { setting ->
            setting.value.draw(guiGraphics, preview = true)
            val hovered = isHovered(setting, mouseX, mouseY)
            val left = resolvedLeft(setting)
            val top = setting.value.y
            val elementWidth = scaledWidth(setting)
            val elementHeight = scaledHeight(setting)
            val borderColor = when {
                setting === activeSetting -> ClickGuiStyle.BORDER_BRIGHT
                hovered -> ClickGuiStyle.SOFT_TEXT
                else -> ClickGuiStyle.MUTED_TEXT
            }

            guiGraphics.fill(left - 1, top - 1, left + elementWidth + 1, top, borderColor)
            guiGraphics.fill(left - 1, top + elementHeight, left + elementWidth + 1, top + elementHeight + 1, borderColor)
            guiGraphics.fill(left - 1, top, left, top + elementHeight, borderColor)
            guiGraphics.fill(left + elementWidth, top, left + elementWidth + 1, top + elementHeight, borderColor)

            val font = Minecraft.getInstance().font
            val alignmentLabel = when (setting.value.alignment) {
                HudAlignment.LEFT -> "L"
                HudAlignment.CENTER -> "C"
                HudAlignment.RIGHT -> "R"
            }
            guiGraphics.drawString(font, alignmentLabel, left + elementWidth + 4, top - 1, ClickGuiStyle.MUTED_TEXT, false)
        }
    }

    private fun findHoveredSetting(mouseX: Float, mouseY: Float): HudSetting? {
        return ModuleManager.hudSettings.asReversed().firstOrNull { isHovered(it, mouseX, mouseY) }
    }

    private fun isHovered(setting: HudSetting, mouseX: Float, mouseY: Float): Boolean {
        val left = resolvedLeft(setting)
        val top = setting.value.y
        val right = left + scaledWidth(setting)
        val bottom = top + scaledHeight(setting)
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom
    }

    private fun scaledWidth(setting: HudSetting): Int {
        return max(1, setting.value.scaledWidth())
    }

    private fun scaledHeight(setting: HudSetting): Int {
        return max(1, setting.value.scaledHeight())
    }

    private fun clampToScreen(setting: HudSetting, snapToGrid: Boolean = true) {
        val maxY = (height - scaledHeight(setting)).coerceAtLeast(0)
        val clampedY = setting.value.y.coerceIn(0, maxY)
        setting.value.y = if (snapToGrid) snap(clampedY).coerceIn(0, maxY) else clampedY

        val elementWidth = scaledWidth(setting)
        when (setting.value.alignment) {
            HudAlignment.LEFT, HudAlignment.RIGHT -> {
                val maxOffset = (width - elementWidth).coerceAtLeast(0)
                val clampedX = setting.value.x.coerceIn(0, maxOffset)
                setting.value.x = if (snapToGrid) snap(clampedX).coerceIn(0, maxOffset) else clampedX
            }
            HudAlignment.CENTER -> {
                val baseLeft = width / 2f - elementWidth / 2f
                val minOffset = -baseLeft
                val maxOffset = width - elementWidth - baseLeft
                val clampedX = setting.value.x.coerceIn(minOffset.toInt(), maxOffset.toInt())
                setting.value.x = if (snapToGrid) {
                    snap(clampedX).coerceIn(minOffset.toInt(), maxOffset.toInt())
                } else {
                    clampedX
                }
            }
        }
    }

    private fun renderCrosshair(guiGraphics: GuiGraphics) {
        (mc.gui as GuiInvoker).`apostle$renderCrosshair`(guiGraphics, mc.deltaTracker)
    }

    private fun snap(value: Int): Int {
        return (value.toFloat() / GRID_SIZE).roundToInt() * GRID_SIZE
    }

    private fun resolvedLeft(setting: HudSetting): Int {
        return setting.value.resolvedX(width)
    }

    private fun xForAlignment(setting: HudSetting, left: Int): Int {
        val elementWidth = scaledWidth(setting)
        return when (setting.value.alignment) {
            HudAlignment.LEFT -> left
            HudAlignment.CENTER -> (left - (width / 2f - elementWidth / 2f)).toInt()
            HudAlignment.RIGHT -> width - elementWidth - left
        }
    }
}
