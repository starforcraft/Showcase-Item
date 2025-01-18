package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.ShowcaseItemFeature;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatComponent.class)
public abstract class ChatComponentMixin {
    // TODO: add chat heads mod compat
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int showcaseItem$drawItems(GuiGraphics graphics, Font font, FormattedCharSequence formattedCharSequence, int x, int y, int color) {
        ShowcaseItemFeature.renderItemForMessage(graphics, formattedCharSequence, x, y, color);

        return graphics.drawString(font, formattedCharSequence, x, y, color);
    }
}
