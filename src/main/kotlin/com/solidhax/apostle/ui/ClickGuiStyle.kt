package com.solidhax.apostle.ui

import com.solidhax.apostle.modules.internal.Category

object ClickGuiStyle {
    const val PANEL_WIDTH = 158
    const val PANEL_HEADER_HEIGHT = 24
    const val MODULE_ROW_HEIGHT = 20
    const val SETTING_ROW_HEIGHT = 18
    const val PANEL_GAP = 14
    const val PANEL_TOP = 68
    const val SCREEN_PADDING = 24

    const val BACKDROP = 0xDD081018.toInt()
    const val BACKDROP_TINT = 0x7A112335
    const val TITLE = 0xFFF4F7FB.toInt()
    const val MUTED_TEXT = 0xFF97A6B6.toInt()
    const val SOFT_TEXT = 0xFFC6D2DF.toInt()
    const val CARD = 0xD8131B24.toInt()
    const val CARD_RAISED = 0xE01A2430.toInt()
    const val CARD_HOVER = 0xF0223040.toInt()
    const val BORDER = 0xFF24384A.toInt()
    const val BORDER_BRIGHT = 0xFF49657C.toInt()
    const val SUCCESS = 0xFF69E6B0.toInt()
    const val DANGER = 0xFFFF8B87.toInt()
    const val SHADOW = 0x70000000

    fun accent(category: Category): Int = when (category) {
        Category.GENERAL -> 0xFF63C8FF.toInt()
        Category.MINING -> 0xFFFFB65C.toInt()
        Category.FARMING -> 0xFF8EE46B.toInt()
        Category.FISHING -> 0xFF61B6FF.toInt()
        Category.DEV -> 0xFFFF6B9E.toInt()
    }

    fun accentSoft(category: Category): Int = withAlpha(accent(category), 96)

    fun withAlpha(color: Int, alpha: Int): Int {
        return (color and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24)
    }
}
