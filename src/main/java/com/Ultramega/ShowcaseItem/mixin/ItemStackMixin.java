package com.Ultramega.ShowcaseItem.mixin;

import com.Ultramega.ShowcaseItem.ShowcaseItemFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getDisplayName()Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
	private void createStackComponent(CallbackInfoReturnable<Component> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ShowcaseItemFeature.createStackComponent((ItemStack) (Object) this, (MutableComponent) callbackInfoReturnable.getReturnValue()));
	}
}