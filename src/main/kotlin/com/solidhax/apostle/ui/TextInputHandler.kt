package com.solidhax.apostle.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.util.StringUtil
import org.lwjgl.glfw.GLFW
import kotlin.math.max
import kotlin.math.min

class TextInputHandler(
    initialText: String = "",
    private val filter: (String) -> String = { it },
    private val maxLength: Int = Int.MAX_VALUE,
    private val onValueChanged: (String) -> Unit = {},
    private val onSubmit: (String) -> Unit = {},
    private val onCancel: (() -> Unit)? = null
) {
    var text: String = sanitize(initialText)
        private set

    var focused = false
        private set

    var x = 0f
    var y = 0f
    var width = 0f
    var height = 18f

    private var caret = text.length
    private var selectionAnchor = text.length
    private var draggingSelection = false

    val hasSelection: Boolean
        get() = caret != selectionAnchor

    val selectionStart: Int
        get() = min(caret, selectionAnchor)

    val selectionEnd: Int
        get() = max(caret, selectionAnchor)

    fun setText(newText: String, moveCaretToEnd: Boolean = false) {
        text = sanitize(newText)
        if (moveCaretToEnd) {
            caret = text.length
            selectionAnchor = caret
        } else {
            caret = caret.coerceIn(0, text.length)
            selectionAnchor = selectionAnchor.coerceIn(0, text.length)
        }
    }

    fun focus(moveCaretToEnd: Boolean = true) {
        focused = true
        if (moveCaretToEnd) {
            caret = text.length
            selectionAnchor = caret
        }
    }

    fun blur() {
        focused = false
        draggingSelection = false
        clearSelection()
    }

    fun mouseClicked(mouseX: Float, mouseY: Float, click: MouseButtonEvent, font: Font): Boolean {
        if (click.button() != 0) {
            return false
        }

        if (!contains(mouseX, mouseY)) {
            blur()
            return false
        }

        focused = true
        draggingSelection = true
        caret = caretFromMouse(mouseX, font)
        selectionAnchor = caret
        return true
    }

    fun mouseDragged(mouseX: Float, mouseY: Float, click: MouseButtonEvent, font: Font): Boolean {
        if (!focused || !draggingSelection || click.button() != 0) {
            return false
        }

        caret = caretFromMouse(mouseX, font)
        return true
    }

    fun mouseReleased(click: MouseButtonEvent) {
        if (click.button() == 0) {
            draggingSelection = false
        }
    }

    fun charTyped(event: CharacterEvent): Boolean {
        if (!focused) {
            return false
        }

        val filtered = sanitize(StringUtil.filterText(event.codepointAsString()))
        if (filtered.isEmpty()) {
            return false
        }

        replaceSelection(filtered)
        return true
    }

    fun keyPressed(event: KeyEvent): Boolean {
        if (!focused) {
            return false
        }

        return when (event.key()) {
            GLFW.GLFW_KEY_BACKSPACE -> {
                if (hasSelection) {
                    replaceSelection("")
                    true
                } else if (caret > 0) {
                    val start = caret - 1
                    replaceRange(start, caret, "")
                    caret = start
                    selectionAnchor = caret
                    true
                } else {
                    false
                }
            }

            GLFW.GLFW_KEY_DELETE -> {
                if (hasSelection) {
                    replaceSelection("")
                    true
                } else if (caret < text.length) {
                    replaceRange(caret, caret + 1, "")
                    true
                } else {
                    false
                }
            }

            GLFW.GLFW_KEY_LEFT -> {
                moveCaret(if (event.hasControlDown()) previousWordStart(caret) else caret - 1, event.hasShiftDown())
                true
            }

            GLFW.GLFW_KEY_RIGHT -> {
                moveCaret(if (event.hasControlDown()) nextWordEnd(caret) else caret + 1, event.hasShiftDown())
                true
            }

            GLFW.GLFW_KEY_HOME -> {
                moveCaret(0, event.hasShiftDown())
                true
            }

            GLFW.GLFW_KEY_END -> {
                moveCaret(text.length, event.hasShiftDown())
                true
            }

            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                onSubmit(text)
                blur()
                true
            }

            GLFW.GLFW_KEY_ESCAPE -> {
                onCancel?.invoke()
                blur()
                true
            }

            else -> handleClipboardShortcut(event)
        }
    }

    fun selectionPixelRange(font: Font): Pair<Int, Int>? {
        if (!hasSelection) {
            return null
        }

        return font.width(text.substring(0, selectionStart)) to font.width(text.substring(0, selectionEnd))
    }

    fun caretPixelX(font: Font): Int {
        return font.width(text.substring(0, caret))
    }

    private fun handleClipboardShortcut(event: KeyEvent): Boolean {
        if (event.isSelectAll()) {
            selectionAnchor = 0
            caret = text.length
            return true
        }

        if (event.isCopy()) {
            Minecraft.getInstance().keyboardHandler.clipboard = selectedText().ifEmpty { text }
            return true
        }

        if (event.isCut()) {
            if (!hasSelection) {
                return false
            }

            Minecraft.getInstance().keyboardHandler.clipboard = selectedText()
            replaceSelection("")
            return true
        }

        if (event.isPaste()) {
            val clipboard = Minecraft.getInstance().keyboardHandler.clipboard
            if (clipboard.isEmpty()) {
                return false
            }

            replaceSelection(clipboard)
            return true
        }

        return false
    }

    private fun moveCaret(target: Int, keepSelection: Boolean) {
        caret = target.coerceIn(0, text.length)
        if (!keepSelection) {
            selectionAnchor = caret
        }
    }

    private fun selectedText(): String {
        return text.substring(selectionStart, selectionEnd)
    }

    private fun replaceSelection(replacement: String) {
        replaceRange(selectionStart, selectionEnd, replacement)
        caret = (selectionStart + sanitize(replacement).length).coerceAtMost(text.length)
        selectionAnchor = caret
    }

    private fun replaceRange(start: Int, end: Int, replacement: String) {
        val sanitized = sanitize(replacement)
        val prefix = text.substring(0, start)
        val suffix = text.substring(end)
        val allowed = (maxLength - prefix.length - suffix.length).coerceAtLeast(0)
        val insert = sanitized.take(allowed)
        text = prefix + insert + suffix
        onValueChanged(text)
    }

    private fun previousWordStart(index: Int): Int {
        var current = index.coerceAtMost(text.length)
        while (current > 0 && text[current - 1].isWhitespace()) current--
        while (current > 0 && !text[current - 1].isWhitespace()) current--
        return current
    }

    private fun nextWordEnd(index: Int): Int {
        var current = index.coerceAtLeast(0)
        while (current < text.length && text[current].isWhitespace()) current++
        while (current < text.length && !text[current].isWhitespace()) current++
        return current
    }

    private fun caretFromMouse(mouseX: Float, font: Font): Int {
        val relativeX = mouseX - x - 4f
        var currentWidth = 0
        for (index in text.indices) {
            val charWidth = font.width(text[index].toString())
            if (relativeX < currentWidth + charWidth / 2f) {
                return index
            }
            currentWidth += charWidth
        }

        return text.length
    }

    private fun contains(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    private fun clearSelection() {
        selectionAnchor = caret
    }

    private fun sanitize(value: String): String {
        return filter(value).take(maxLength)
    }
}
