package com.ultramega.showcaseitem.network;

import com.ultramega.showcaseitem.ShowcaseItem;
import com.ultramega.showcaseitem.ShowcaseItemFeature;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ShareItemData(int slot, int containerId) implements CustomPacketPayload {
    public static final Type<ShareItemData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ShowcaseItem.MODID, "share_item_data"));

    public static final StreamCodec<ByteBuf, ShareItemData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShareItemData::slot,
            ByteBufCodecs.INT, ShareItemData::containerId,
            ShareItemData::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if(player instanceof ServerPlayer serverPlayer) {
                ShowcaseItemFeature.shareItem(serverPlayer, slot, containerId);
            }
        }).exceptionally(e -> null);
    }
}
