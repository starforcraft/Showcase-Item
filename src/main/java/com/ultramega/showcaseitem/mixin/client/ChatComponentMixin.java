package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.ShowcaseItemFeatureClient;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;"
        + "IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At("HEAD"))
    private static void chatheads$captureGuiGraphics(final CallbackInfo ci, final @Local(argsOnly = true) GuiGraphicsExtractor graphics) {
        ShowcaseItemFeatureClient.graphics = graphics;
    }

    @Inject(method = "captureClickableText", at = @At("HEAD"))
    private static void chatheads$noGraphics(final CallbackInfo ci) {
        ShowcaseItemFeatureClient.graphics = null;
    }
}
