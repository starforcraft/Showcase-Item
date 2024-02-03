package com.ultramega.showcaseitem.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.ultramega.showcaseitem.ShowcaseItemFeature;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyExpressionValue(method = "renderQuadList", at = @At(value = "CONSTANT", args = "floatValue=1F"), require = 0)
    // Allow failure in case of rubidium/embeddium
    public float renderQuads(float original) {
        return ShowcaseItemFeature.alphaValue * original;
    }
}
