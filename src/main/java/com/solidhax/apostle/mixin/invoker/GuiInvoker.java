package com.solidhax.apostle.mixin.invoker;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiInvoker {

    @Invoker("renderCrosshair")
    void apostle$renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}