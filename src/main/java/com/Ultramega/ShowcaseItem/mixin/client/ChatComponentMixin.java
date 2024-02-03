package com.ultramega.showcaseitem.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ultramega.showcaseitem.ShowcaseItemFeature;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int drawItems(GuiGraphics guiGraphics, Font font, FormattedCharSequence formattedCharSequence, int x, int y, int color, Operation<Integer> original) {
        ShowcaseItemFeature.renderItemForMessage(guiGraphics, formattedCharSequence, x, y, color);

        return original.call(guiGraphics, font, formattedCharSequence, x, y, color);
    }
}
