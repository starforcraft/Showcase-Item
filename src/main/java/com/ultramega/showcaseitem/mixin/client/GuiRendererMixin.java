package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.interfaces.AlphaItemStackRenderState;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.gui.render.GuiItemAtlas.SlotView;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @Shadow
    @Final
    private GuiRenderState renderState;

    @Inject(method = "submitBlitFromItemAtlas", at = @At("HEAD"), cancellable = true, remap = false)
    private void submitBlitFromItemAtlas(final GuiItemRenderState itemState, final SlotView slotView, final CallbackInfo ci) {
        if (!(itemState.itemStackRenderState() instanceof AlphaItemStackRenderState alphaItemStackRenderState)) {
            return;
        }

        final float alpha = alphaItemStackRenderState.showcaseitem$getAlpha();
        if (alpha >= 1.0f) {
            return;
        }

        this.renderState.addBlitToCurrentLayer(new BlitRenderState(
            RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
            TextureSetup.singleTexture(slotView.textureView(), RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
            itemState.pose(),
            itemState.x(),
            itemState.y(),
            itemState.x() + 16,
            itemState.y() + 16,
            slotView.u0(),
            slotView.u1(),
            slotView.v0(),
            slotView.v1(),
            ARGB.color(Math.round(255 * alpha), Math.round(255 * alpha), Math.round(255 * alpha), Math.round(255 * alpha)),
            itemState.scissorArea(),
            null)
        );

        ci.cancel();
    }
}
