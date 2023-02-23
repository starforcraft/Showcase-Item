package com.Ultramega.ShowcaseItem.message;

import com.Ultramega.ShowcaseItem.ShowcaseItem;
import net.minecraftforge.network.NetworkDirection;

public class ShowcaseItemNetwork {
    private static final int PROTOCOL_VERSION = 1;

    private static NetworkHandler network;

    public static void setup() {
        network = new NetworkHandler(ShowcaseItem.MOD_ID, PROTOCOL_VERSION);

        network.register(ShareItemMessage.class, NetworkDirection.PLAY_TO_SERVER);
    }

    public static void sendToServer(IMessage msg) {
        network.sendToServer(msg);
    }
}
