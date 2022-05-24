package com.Ultramega.ShowcaseItem.network;

import java.util.function.Supplier;

import com.Ultramega.ShowcaseItem.ShowcaseItemFeature;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class LinkItemMessage {
	public ItemStack stack;

	public LinkItemMessage() {
	}

	public LinkItemMessage(ItemStack stack) {
		this.stack = stack;
	}

	public static void encode(LinkItemMessage message, PacketBuffer buffer) {
		buffer.writeItem(message.stack);
	}

	public static LinkItemMessage decode(PacketBuffer buffer) {
		return new LinkItemMessage(buffer.readItem());
	}

	public static void handle(LinkItemMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> ShowcaseItemFeature.linkItem(context.getSender(), message.stack));
		context.setPacketHandled(true);
	}
}
