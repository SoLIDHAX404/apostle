package com.solidhax.apostle.modules.dev

import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting

object Test : Module("Test", "test", Category.DEV) {
    val testSetting by BooleanSetting("Test Setting", "Development toggle", false)
    val testSetting2 by BooleanSetting("Test Setting 2", "Development toggle", false)
}