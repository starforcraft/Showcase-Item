package com.ultramega.showcaseitem.mixin.client;

import com.ultramega.showcaseitem.ShowcaseItemFeatureClient;

import net.minecraft.client.gui.components.ChatComponent.ChatGraphicsAccess;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$1")
public abstract class ChatComponentMixin2 {
    @Redirect(method = "accept",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;handleMessage(IFLnet/minecraft/util/FormattedCharSequence;)Z"))
    private boolean showcaseItem$drawItems(final ChatGraphicsAccess graphics, final int textTop, final float opacity, final FormattedCharSequence message) {
        ShowcaseItemFeatureClient.renderItemForMessage(message, textTop, opacity);

        return graphics.handleMessage(textTop, opacity, message);
    }
}
