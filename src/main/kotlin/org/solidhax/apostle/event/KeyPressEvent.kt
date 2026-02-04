package org.solidhax.apostle.event

import com.mojang.blaze3d.platform.InputConstants
import org.solidhax.apostle.event.impl.CancellableEvent

class KeyPressEvent(val key: InputConstants.Key) : CancellableEvent()