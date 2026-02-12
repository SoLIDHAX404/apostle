package org.solidhax.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.solidhax.apostle.event.GuiEvent;
import org.solidhax.apostle.modules.render.HUD;
import org.solidhax.apostle.modules.render.HudType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V"), cancellable = true)
    private void onRenderSlot(GuiGraphics guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k, CallbackInfo ci) {
        if(new GuiEvent.RenderSlot((Gui)(Object)this, guiGraphics, i, j, itemStack).postAndCatch()) ci.cancel();
    }

    @Inject(method = "renderHearts", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderHearts(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.HEARTS)) ci.cancel();
    }

    @Inject(method = "renderArmor", at = @At(value = "HEAD"), cancellable = true)
    private static void onRenderArmor(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.ARMOR)) ci.cancel();
    }

    @Inject(method = "renderFood", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderFood(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.HUNGER)) ci.cancel();
    }

    @Inject(method = "renderEffects", at = @At(value = "HEAD"), cancellable = true)
    private void onRenderEffects(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.POTION_EFFECTS)) ci.cancel();
    }

    @Inject(method = "renderSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"), cancellable = true)
    private void onRenderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(HUD.shouldCancelHud(HudType.HELD_ITEM_TOOLTIP)) ci.cancel();
    }
}
