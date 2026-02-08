package org.solidhax.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.solidhax.apostle.modules.render.HUD;
import org.solidhax.apostle.modules.render.HudType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void onRenderEffects(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.INVENTORY_POTION_EFFECTS)) ci.cancel();
    }
}
