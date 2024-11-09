package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.ShowcaseItemFeature;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatComponent.class)
public abstract class ChatComponentMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int showcaseItem$drawItems(GuiGraphics graphics, Font font, FormattedCharSequence formattedCharSequence, int x, int y, int color) {
        int xOffset = ModList.get().isLoaded("chat_heads") ? 10 : 0;
        ShowcaseItemFeature.renderItemForMessage(graphics, formattedCharSequence, x + xOffset, y, color);

        return graphics.drawString(font, formattedCharSequence, x, y, color);
    }
}
