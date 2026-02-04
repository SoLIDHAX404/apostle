package org.solidhax.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.solidhax.apostle.event.KeyPressEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Inject(method = "click", at = @At("HEAD"), cancellable = true)
    private static void onClick(InputConstants.Key key, CallbackInfo ci) {
        if(new KeyPressEvent(key).post()) ci.cancel();
    }
}
