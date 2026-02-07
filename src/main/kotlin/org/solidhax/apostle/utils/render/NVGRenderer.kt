package org.solidhax.apostle.utils.render

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.solidhax.apostle.Apostle.Companion.mc
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

/*
Heavily inspired by OdinFabric under the BSD 3-Clause
https://github.com/odtheking/OdinFabric
*/

object NVGRenderer {
    private val nvgPaint = NVGPaint.malloc()
    private val nvgColor = NVGColor.malloc()

    val defaultFont = Font("Default", mc.resourceManager.getResource(ResourceLocation.parse("apostle:font.ttf")).get().open())
    private val fontMap = HashMap<Font, NVGFont>()
    private val fontBounds = FloatArray(4)

    private var scissor: Scissor? = null
    private var drawing: Boolean = false
    private var vg = -1L

    init {
        vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
        require(vg != -1L) { "Failed to initialize nano vg"}
    }

    fun devicePixelRatio(): Float {
        return try {
            val window = Minecraft.getInstance().window
            val fbw = window.width
            val ww = window.screenWidth
            if (ww == 0) 1f else fbw.toFloat() / ww.toFloat()
        } catch (_: Throwable) {
            1f
        }
    }

    fun beginFrame(width: Float, height: Float) {
        if(drawing) throw IllegalStateException("Called beginFrame whilst already drawing")

        val dpr = devicePixelRatio()

        nvgBeginFrame(vg, width / dpr, height / dpr, dpr)
        nvgTextAlign(vg, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
        drawing = true
    }

    fun endFrame() {
        if(!drawing) throw IllegalStateException("Called endFrame whilst not drawing")
        nvgEndFrame(vg)
        drawing = false
    }

    fun filledRect(x: Float, y: Float, w: Float, h: Float, color: Color, radius: Float) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgFillColor(vg, nvgColor)
        nvgFill(vg)
    }

    fun rect(x: Float, y: Float, w: Float, h: Float, thickness: Float, color: Color, radius: Float) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h, radius)
        nvgStrokeWidth(vg, thickness)
        nvgPathWinding(vg, NVG_HOLE)
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgStrokeColor(vg, nvgColor)
        nvgStroke(vg)
    }

    fun circle(x: Float, y: Float, radius: Float, color: Color) {
        nvgBeginPath(vg)
        nvgCircle(vg, x, y, radius)
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgFillColor(vg, nvgColor)
        nvgFill(vg)
    }

    fun text(text: String, x: Float, y: Float, size: Float, color: Color, font: Font) {
        nvgFontSize(vg, size)
        nvgFontFaceId(vg, getFontID(font))
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgFillColor(vg, nvgColor)
        nvgText(vg, x, y + .5f, text)
    }

    fun textWidth(text: String, size: Float, font: Font): Float {
        nvgFontSize(vg, size)
        nvgFontFaceId(vg, getFontID(font))
        return nvgTextBounds(vg, 0f, 0f, text, fontBounds)
    }

    private fun getFontID(font: Font): Int {
        return fontMap.getOrPut(font) {
            val buffer = font.buffer()
            NVGFont(nvgCreateFontMem(vg, font.name, buffer, false), buffer)
        }.id
    }

    private class Scissor(val previous: Scissor?, val x: Float, val y: Float, val maxX: Float, val maxY: Float) {
        fun applyScissor() {
            if (previous == null) nvgScissor(vg, x, y, maxX - x, maxY - y)
            else {
                val x = max(x, previous.x)
                val y = max(y, previous.y)
                val width = max(0f, (min(maxX, previous.maxX) - x))
                val height = max(0f, (min(maxY, previous.maxY) - y))
                nvgScissor(vg, x, y, width, height)
            }
        }
    }

    private data class NVGFont(val id: Int, val buffer: ByteBuffer)
}