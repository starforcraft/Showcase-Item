package com.Ultramega.ShowcaseItem.mixin;

import com.Ultramega.ShowcaseItem.ShowcaseItemFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Inject(method = "getDisplayName()Lnet/minecraft/util/text/ITextComponent;", at = @At("RETURN"), cancellable = true)
	private void createStackComponent(CallbackInfoReturnable<ITextComponent> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ShowcaseItemFeature.createStackComponent((ItemStack) (Object) this, (IFormattableTextComponent) callbackInfoReturnable.getReturnValue()));
	}
}