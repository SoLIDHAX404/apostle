package com.solidhax.apostle.ui.setting

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.solidhax.apostle.Apostle.mc
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.ClickGuiStyle
import com.solidhax.apostle.ui.HudManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class HudSetting(
    name: String,
    description: String = "",
    x: Int,
    y: Int,
    scale: Float = 1f,
    enabled: Boolean = false,
    draw: GuiGraphics.(Boolean) -> Pair<Int, Int>
) : RenderableSetting<HudElement>(name, description), Savable {

    override val default: HudElement = HudElement(x, y, scale, enabled, draw)
    override var value: HudElement = HudElement(x, y, scale, enabled, draw)
    private var owner: Module? = null

    val isEnabled: Boolean
        get() = owner?.enabled == true && value.enabled

    override operator fun provideDelegate(thisRef: Module, property: KProperty<*>): ReadWriteProperty<Module, HudElement> {
        owner = thisRef
        return super.provideDelegate(thisRef, property)
    }

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, mouseX: Float, mouseY: Float) {
        lastX = x
        lastY = y

        val left = x.toInt()
        val top = y.toInt()
        val right = left + width
        val bottom = top + ClickGuiStyle.SETTING_ROW_HEIGHT
        val hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + ClickGuiStyle.SETTING_ROW_HEIGHT
        val font = Minecraft.getInstance().font
        val trackLeft = right - 34
        val trackRight = right - 10
        val trackTop = top + 4
        val trackBottom = bottom - 4
        val knobLeft = if (value.enabled) trackRight - 10 else trackLeft + 1

        guiGraphics.fill(left, top, right, bottom, if (hovered) 0x70334759 else 0x50273444)
        guiGraphics.drawString(font, name, left + 8, top + 5, ClickGuiStyle.SOFT_TEXT, false)
        guiGraphics.fill(trackLeft, trackTop, trackRight, trackBottom, if (value.enabled) ClickGuiStyle.withAlpha(ClickGuiStyle.SUCCESS, 180) else 0x90404C57.toInt())
        guiGraphics.fill(knobLeft, trackTop + 1, knobLeft + 9, trackBottom - 1, ClickGuiStyle.TITLE)
    }

    override fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent): Boolean {
        val inside = mouseX >= lastX && mouseX <= lastX + width &&
            mouseY >= lastY && mouseY <= lastY + ClickGuiStyle.SETTING_ROW_HEIGHT
        if (!inside) {
            return false
        }

        if (click.button() == 1) {
            mc.setScreen(HudManager)
            return true
        }

        if (click.button() != 0) {
            return false
        }

        value.enabled = !value.enabled
        return true
    }

    override fun reset() {
        value = HudElement(default.x, default.y, default.scale, default.enabled, default.render)
    }

    override fun read(element: JsonElement, gson: Gson) {
        val obj = element.takeIf { it.isJsonObject }?.asJsonObject ?: return
        value.x = obj.get("x")?.asInt ?: value.x
        value.y = obj.get("y")?.asInt ?: value.y
        value.scale = obj.get("scale")?.asFloat ?: value.scale
        value.enabled = obj.get("enabled")?.asBoolean ?: value.enabled
    }

    override fun write(gson: Gson): JsonElement = JsonObject().apply {
        addProperty("x", value.x)
        addProperty("y", value.y)
        addProperty("scale", value.scale)
        addProperty("enabled", value.enabled)
    }
}
