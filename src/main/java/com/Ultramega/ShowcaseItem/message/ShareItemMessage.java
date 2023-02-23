package com.Ultramega.ShowcaseItem.message;

import com.Ultramega.ShowcaseItem.ShowcaseItemFeature;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

import java.io.Serial;

public class ShareItemMessage implements IMessage {
    @Serial
    private static final long serialVersionUID = 2204175080232208579L;

    public int slot;

    public ShareItemMessage(int slot) {
        this.slot = slot;
    }

    public boolean receive(Context context) {
        ServerPlayer player = context.getSender();
        if (player != null && player.server != null)
            context.enqueueWork(() -> ShowcaseItemFeature.shareItem(player, slot));

        return true;
    }
}