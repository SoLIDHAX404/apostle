package org.solidhax.apostle.utils.render

import net.minecraft.client.Minecraft
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import kotlin.math.max
import kotlin.math.min

/*
Heavily inspired by OdinFabric under the BSD 3-Clause
https://github.com/odtheking/OdinFabric
*/

object NVGRenderer {
    private val nvgPaint = NVGPaint.malloc()
    private val nvgColor = NVGColor.malloc()

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

    fun rect(x: Float, y: Float, w: Float, h: Float, color: Color, radius: Float) {
        nvgBeginPath(vg)
        nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgFillColor(vg, nvgColor)
        nvgFill(vg)
    }

    fun circle(x: Float, y: Float, radius: Float, color: Color) {
        nvgBeginPath(vg)
        nvgCircle(vg, x, y, radius)
        nvgRGBA(color.redB(), color.greenB(), color.blueB(), color.alphaB(), nvgColor)
        nvgFillColor(vg, nvgColor)
        nvgFill(vg)
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
}