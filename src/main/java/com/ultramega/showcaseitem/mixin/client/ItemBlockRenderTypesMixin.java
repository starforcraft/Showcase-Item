package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.ShowcaseItemFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    @Inject(method = "getRenderType(Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/client/renderer/RenderType;", at = @At("HEAD"), cancellable = true)
    private static void overrideRenderType(BlockState state, boolean needsCulling, CallbackInfoReturnable<RenderType> cir) {
        if (ShowcaseItemFeature.alphaValue != 1.0F) {
            if (!Minecraft.useShaderTransparency()) {
                cir.setReturnValue(Sheets.translucentCullBlockSheet());
            } else {
                cir.setReturnValue(needsCulling ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet());
            }
        }
    }
}
