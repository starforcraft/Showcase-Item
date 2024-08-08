package com.ultramega.showcaseitem.config;

import com.ultramega.showcaseitem.ShowcaseItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ShowcaseItem.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue RENDER_ITEMS_IN_CHAT = BUILDER
            .comment("Render items in chat")
            .define("renderItemsInChat", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean renderItemsInChat;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        renderItemsInChat = RENDER_ITEMS_IN_CHAT.get();
    }
}
