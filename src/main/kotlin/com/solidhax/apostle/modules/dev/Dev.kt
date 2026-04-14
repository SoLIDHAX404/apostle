package com.solidhax.apostle.modules.dev

import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.HudSetting
import net.minecraft.client.Minecraft

object Dev : Module("Dev", "Dev Module to test all Settings", Category.DEV) {

    val exampleHud by HudSetting("Example Hud", "Module HUD", 8, 8, 1f, true) { preview ->
        val font = Minecraft.getInstance().font
        val text = "Live HUD"
        drawString(font, text, 0, 0, 0xFFFFFFFF.toInt(), false)
        font.width(text) to font.lineHeight
    }

    val testSetting by BooleanSetting("Test", "Save test", false)

}