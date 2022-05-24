package com.Ultramega.ShowcaseItem.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ShowcaseItemConfig {
    public static ForgeConfigSpec.BooleanValue RENDER_ITEMS_IN_CHAT;

    public static void init(ForgeConfigSpec.Builder common) {
        common.comment("Showcase Item Options");

        RENDER_ITEMS_IN_CHAT = common
                .comment("\nRender Items in Chat")
                .define("renderItemsInChat", true);

        common.build();
    }
}
