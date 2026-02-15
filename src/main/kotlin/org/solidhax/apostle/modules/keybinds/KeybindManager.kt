package org.solidhax.apostle.modules.keybinds

import com.google.gson.reflect.TypeToken
import com.mojang.blaze3d.platform.InputConstants
import org.solidhax.apostle.Apostle.Companion.CONFIG_DIR
import org.solidhax.apostle.Apostle.Companion.gson
import org.solidhax.apostle.event.ClientEvent
import org.solidhax.apostle.event.InputEvent
import org.solidhax.apostle.event.impl.on
import org.solidhax.apostle.utils.chat.sendCommand
import org.solidhax.apostle.utils.location.LocationUtils
import java.nio.file.Files

data class Keybind(
    var command: String = "",
    var keyType: InputConstants.Type = InputConstants.Type.KEYSYM,
    var keyValue: Int = InputConstants.UNKNOWN.value,
    var locations: List<String> = emptyList(),
    var enabled: Boolean = true
) {
    val key: InputConstants.Key
        get() = keyType.getOrCreate(keyValue)
}

object KeybindManager {
    var keybinds: MutableList<Keybind> = mutableListOf()

    init {
        on<InputEvent> {
            val pressedKey = key

            val keybind = keybinds.firstOrNull {
                it.enabled &&
                        it.keyType == pressedKey.type &&
                        it.keyValue == pressedKey.value &&
                        (it.locations.isEmpty() || it.locations.contains(LocationUtils.currentArea.displayName))
            }

            keybind?.let {
                sendCommand(it.command)
            }
        }

        on<ClientEvent.Start> {
            loadKeybinds()
        }

        on<ClientEvent.Stop> {
            saveKeybinds()
        }
    }

    fun saveKeybinds() {
        Files.createDirectories(CONFIG_DIR)

        val json = gson.toJson(keybinds)
        val file = CONFIG_DIR.resolve("keybinds.json")
        Files.write(file, json.toByteArray())
    }

    fun loadKeybinds() {
        val file = CONFIG_DIR.resolve("keybinds.json")
        if (!Files.exists(file)) return

        val json = Files.readString(file)

        val type = object : TypeToken<MutableList<Keybind>>() {}.type
        keybinds = gson.fromJson(json, type) ?: mutableListOf()
    }
}