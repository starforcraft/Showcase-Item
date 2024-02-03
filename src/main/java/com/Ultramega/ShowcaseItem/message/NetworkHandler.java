/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 * <p>
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 */
package com.ultramega.showcaseitem.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    public final SimpleChannel channel;

    private int i = 0;

    public NetworkHandler(String modid, int protocol) {
        this(modid, "main", protocol);
    }

    public NetworkHandler(String modid, String channelName, int protocol) {
        String protocolStr = Integer.toString(protocol);

        channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modid, channelName))
                .networkProtocolVersion(() -> protocolStr)
                .clientAcceptedVersions(protocolStr::equals)
                .serverAcceptedVersions(protocolStr::equals)
                .simpleChannel();
    }

    public <T extends IMessage> void register(Class<T> clazz, NetworkDirection dir) {
        BiConsumer<T, FriendlyByteBuf> encoder = MessageSerializer::writeObject;

        Function<FriendlyByteBuf, T> decoder = (buf) -> {
            try {
                T msg = clazz.getDeclaredConstructor().newInstance();
                MessageSerializer.readObject(msg, buf);
                return msg;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        };

        BiConsumer<T, Supplier<NetworkEvent.Context>> consumer = (msg, supp) -> {
            NetworkEvent.Context context = supp.get();
            if (context.getDirection() != dir)
                return;

            context.setPacketHandled(msg.receive(context));
        };

        channel.registerMessage(i, clazz, encoder, decoder, consumer);
        i++;
    }

    public void sendToServer(IMessage msg) {
        channel.sendToServer(msg);
    }
}