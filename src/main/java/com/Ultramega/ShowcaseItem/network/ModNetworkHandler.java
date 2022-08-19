package com.Ultramega.ShowcaseItem.network;

import com.Ultramega.ShowcaseItem.ShowcaseItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkHandler {
	public static final String protocolVersion = Integer.toString(1);
	private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(ShowcaseItem.MOD_ID, "main_channel"))
			.clientAcceptedVersions(protocolVersion::equals)
			.serverAcceptedVersions(protocolVersion::equals)
			.networkProtocolVersion(() -> protocolVersion)
			.simpleChannel();

	public void init() {
		this.handler.registerMessage(0, LinkItemMessage.class, LinkItemMessage::encode, LinkItemMessage::decode, LinkItemMessage::handle);
	}

	public void sendToServer(Object message) {
		this.handler.sendToServer(message);
	}
}
