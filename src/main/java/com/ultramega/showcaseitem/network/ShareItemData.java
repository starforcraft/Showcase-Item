package com.ultramega.showcaseitem.network;

import com.ultramega.showcaseitem.ShowcaseItem;
import com.ultramega.showcaseitem.ShowcaseItemFeatureServer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShareItemData(int slot, int containerId) implements CustomPacketPayload {
    public static final Type<ShareItemData> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ShowcaseItem.MODID, "share_item_data"));

    public static final StreamCodec<ByteBuf, ShareItemData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShareItemData::slot,
            ByteBufCodecs.INT, ShareItemData::containerId,
            ShareItemData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            final Player player = context.player();
            if (player instanceof ServerPlayer serverPlayer) {
                ShowcaseItemFeatureServer.shareItem(serverPlayer, slot, containerId);
            }
        });
    }
}
