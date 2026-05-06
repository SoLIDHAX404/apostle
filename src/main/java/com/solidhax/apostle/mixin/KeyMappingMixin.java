package com.solidhax.apostle.mixin;

import com.solidhax.apostle.modules.farming.FarmKeys;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void onIsDown(CallbackInfoReturnable<Boolean> cir) {
        Boolean result = FarmKeys.getSwappedIsDown((KeyMapping) (Object) this);
        if (result != null) cir.setReturnValue(result);
    }

    @Inject(method = "consumeClick", at = @At("HEAD"), cancellable = true)
    private void onConsumeClick(CallbackInfoReturnable<Boolean> cir) {
        Boolean result = FarmKeys.getSwappedConsumeClick((KeyMapping) (Object) this);
        if (result != null) cir.setReturnValue(result);
    }
}
