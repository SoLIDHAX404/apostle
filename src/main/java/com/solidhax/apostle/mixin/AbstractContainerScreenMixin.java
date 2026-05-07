package com.solidhax.apostle.mixin;

import com.solidhax.apostle.events.GuiEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.solidhax.apostle.utils.ChatUtilsKt.playerMessage;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void onRenderSlot(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
        if(new GuiEvent.RenderSlot((Screen) (Object)this, guiGraphics, slot).post()) ci.cancel();
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void onSlotClicked(Slot slot, int i, int j, ClickType clickType, CallbackInfo ci) {
        if(new GuiEvent.SlotClicked((Screen) (Object)this, slot, clickType).post()) ci.cancel();
    }

}
