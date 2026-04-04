package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.interfaces.AlphaItemStackRenderState;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin implements AlphaItemStackRenderState {
    @Unique
    private float showcaseitem$alpha = 1.0f;

    @Override
    public void showcaseitem$setAlpha(final float alpha) {
        this.showcaseitem$alpha = alpha;
    }

    @Override
    public float showcaseitem$getAlpha() {
        return showcaseitem$alpha;
    }
}
