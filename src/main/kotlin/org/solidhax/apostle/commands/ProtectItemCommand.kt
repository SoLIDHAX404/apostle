package org.solidhax.apostle.commands

import com.github.stivais.commodore.Commodore
import org.solidhax.apostle.modules.skyblock.ProtectItem
import org.solidhax.apostle.utils.scheduler.schedule

val protectItemCommand = Commodore("protectitem") {
    runs {
        schedule(0) { ProtectItem.onProtectItem() }
    }
}