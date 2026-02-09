package org.solidhax.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.solidhax.apostle.event.AbstractContainerScreenEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Nullable
    @Shadow
    protected Slot hoveredSlot;

    @Final
    @Shadow
    protected AbstractContainerMenu menu;

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void onRenderSlot(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if(new AbstractContainerScreenEvent.RenderSlot((Screen) (Object)this, guiGraphics, slot).post()) ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if(new AbstractContainerScreenEvent.KeyPress((Screen) (Object)this, keyEvent, hoveredSlot).post()) cir.setReturnValue(false);
    }

    @Inject(method = "slotClicked", at = @At(value = "HEAD"), cancellable = true)
    private void onSlotClicked(Slot slot, int slotId, int button, ClickType clickType, CallbackInfo ci) {
        if(new AbstractContainerScreenEvent.SlotClicked((Screen) (Object)this, slotId, clickType, menu.getCarried()).post()) ci.cancel();
    }
}
