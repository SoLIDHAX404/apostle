package org.solidhax.apostle.config

import com.mojang.blaze3d.platform.InputConstants
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.universal.UKeyboard
import gg.essential.vigilance.gui.settings.CheckboxComponent
import gg.essential.vigilance.utils.onLeftClick
import org.solidhax.apostle.config.components.Button
import org.solidhax.apostle.config.components.MultiSelectDropdown
import org.solidhax.apostle.modules.macros.Keybind
import org.solidhax.apostle.modules.macros.KeybindManager
import org.solidhax.apostle.utils.location.Area

object KeybindConfig : WindowScreen(ElementaVersion.V2) {

    private val scrollComponent: ScrollComponent
    private var clickedButton: Entry? = null
    private val components = HashMap<UIContainer, Entry>()

    init {
        UIText("Keybinds").childOf(window).constrain {
            x = CenterConstraint()
            y = RelativeConstraint(0.075f)
            height = 14.pixels()
        }

        scrollComponent = ScrollComponent(innerPadding = 4f).childOf(window).constrain {
            x = CenterConstraint()
            y = 15.percent()
            width = 90.percent()
            height = 70.percent() + 2.pixels()
        }

        val bottomButtons = UIContainer().childOf(window).constrain {
            x = CenterConstraint()
            y = 90.percent()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        Button("Save and Exit").childOf(bottomButtons).constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onLeftClick {
            displayScreen(null)
        }

        Button("Add Shortcut").childOf(bottomButtons).constrain {
            x = SiblingConstraint(5f)
            y = 0.pixels()
        }.onLeftClick {
            addKeybindToList()
        }

        KeybindManager.keybinds.forEach { keybind ->
            addKeybindToList(keybind.command, keybind.key, keybind.locations, keybind.enabled)
        }
    }

    private fun addKeybindToList(command: String = "", key: InputConstants.Key = InputConstants.UNKNOWN, locations: List<String> = emptyList(), enabled: Boolean = true) {
        val container = UIContainer().childOf(scrollComponent).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(5f)
            width = 80.percent()
            height = 9.5.percent()
        }

        val toggle = CheckboxComponent(enabled).childOf(container).constrain {
            x = 5.pixels()
            y = CenterConstraint()
        }

        val commandToRun = (UITextInput("Command to execute").childOf(container).constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
            width = 60.percent()
        }.onLeftClick {
            if(clickedButton == null) grabWindowFocus()
        } as UITextInput).also {
            it.setText(command)
            it.onKeyType { _, _ ->
                it.setText(it.getText().take(256))
            }
        }

        val locations = MultiSelectDropdown("Location", Area.withoutUnknown().map { it.displayName }, locations.toSet()).childOf(container).constrain {
            x = SiblingConstraint(7.5f)
            y = CenterConstraint()
            width = 140.pixels()
            height = 75.percent()
        }.also { it.attachToRoot(window) }

        val keybindButton = Button(InputConstants.UNKNOWN.displayName.string).childOf(container).constrain {
            x = SiblingConstraint(7.5f)
            y = CenterConstraint()
            height = 75.percent()
        }

        Button("Remove").childOf(container).constrain {
            x = SiblingConstraint(7.5f)
            y = CenterConstraint()
            height = 75.percent()
        }.onLeftClick {
            container.parent.removeChild(container)
            components.remove(container)
        }

        val entry = Entry(container, commandToRun, keybindButton, locations, key, toggle)

        keybindButton.onLeftClick {
            clickedButton = entry
        }

        components[container] = entry
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {

        clickedButton?.let { entry ->
            entry.key = if (keyCode == UKeyboard.KEY_ESCAPE) InputConstants.UNKNOWN else InputConstants.Type.KEYSYM.getOrCreate(keyCode)
            clickedButton = null
            return
        }

        super.onKeyPressed(keyCode, typedChar, modifiers)
    }

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {

        clickedButton?.let { entry ->
            entry.key = InputConstants.Type.MOUSE.getOrCreate(mouseButton)
            clickedButton = null
            return
        }

        super.onMouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun onTick() {
        super.onTick()
        for((_, entry) in components) {
            val button = entry.button
            button.text.setText(entry.getDisplayName())
            val pressed = clickedButton === entry
            if(pressed) {
                button.text.setText(button.text.getText())
            }
        }
    }

    override fun onScreenClose() {
        super.onScreenClose()
        KeybindManager.keybinds.clear()

        for((_, entry) in components) {
            val command = entry.input.getText()
            val key = entry.key
            val locations = entry.locationDropdown.getSelected()
            if(command.isBlank() || key == InputConstants.UNKNOWN) continue

            KeybindManager.keybinds.add(Keybind(command, key.type, key.value, locations.toList(), entry.toggle.checked))
        }
    }

    data class Entry(val container: UIContainer, val input: UITextInput, val button: Button, val locationDropdown: MultiSelectDropdown, var key: InputConstants.Key, val toggle: CheckboxComponent) {
        fun getDisplayName(): String = key.displayName.string
    }
}