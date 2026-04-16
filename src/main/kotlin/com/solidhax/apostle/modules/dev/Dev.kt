package com.solidhax.apostle.modules.dev

import com.solidhax.apostle.modules.internal.Category
import com.solidhax.apostle.modules.internal.Module
import com.solidhax.apostle.ui.setting.BooleanSetting
import com.solidhax.apostle.ui.setting.ColorSetting
import com.solidhax.apostle.ui.setting.DropdownSetting
import com.solidhax.apostle.ui.setting.SliderSetting
import java.awt.Color

object Dev : Module("Dev", "Dev Module to test all Settings", Category.DEV) {
    val testSettingBoolean by BooleanSetting("Boolean", "Save test", false)
    val testSettingSlider by SliderSetting("Slider", "Slider", 15f, 0f, 100f)
    val testDropDown by DropdownSetting<String>("Dropdown", "Dropdown", "Test", listOf("Test", "Test 2", "Test 3"))
    val testDropDownMulti by DropdownSetting<String>("DropdownMulti", "Dropdown", "Test", listOf("Test", "Test 2", "Test 3"), true)
    val testColorSetting by ColorSetting("Color", "Color Settings", Color.RED)
}