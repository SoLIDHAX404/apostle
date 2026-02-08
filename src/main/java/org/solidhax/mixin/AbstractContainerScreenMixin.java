package org.solidhax.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.solidhax.apostle.event.AbstractContainerScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void onRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if(new AbstractContainerScreenEvent.RenderSlot((Screen) (Object)this, guiGraphics, slot).post()) ci.cancel();
    }
}
