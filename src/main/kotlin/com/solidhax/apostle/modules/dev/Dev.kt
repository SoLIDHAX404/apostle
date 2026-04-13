package com.solidhax.apostle.modules.dev

import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.DropdownSetting
import com.solidhax.apostle.ui.setting.SliderSetting

object Dev : Module("Test", "Test Module", Category.DEV) {
    val testSetting by BooleanSetting("Test Setting", "Development toggle", false)
    val testSetting2 by BooleanSetting("Test Setting 2", "Development toggle", false)
    val testSetting3 by SliderSetting("Test", "test", 15f, 0f, 100f)
    val mode by DropdownSetting("Mode", "Choose a mode", "Normal", listOf("Normal", "Fast", "Safe"), true)
}
