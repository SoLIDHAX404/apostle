package com.solidhax.apostle.mixin.accessor;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("isDown")
    boolean getIsDown();

    @Accessor("clickCount")
    int getClickCount();

    @Accessor("clickCount")
    void setClickCount(int clickCount);
}