package com.Ultramega.ShowcaseItem.network;

import com.Ultramega.ShowcaseItem.ShowcaseItemFeature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LinkItemMessage {
	public ItemStack stack;

	public LinkItemMessage(ItemStack stack) {
		this.stack = stack;
	}

	public static void encode(LinkItemMessage message, FriendlyByteBuf buffer) {
		buffer.writeItem(message.stack);
	}

	public static LinkItemMessage decode(FriendlyByteBuf buffer) {
		return new LinkItemMessage(buffer.readItem());
	}

	public static void handle(LinkItemMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> ShowcaseItemFeature.linkItem(context.getSender(), message.stack));
		context.setPacketHandled(true);
	}
}
