package com.ultramega.showcaseitem;

import com.ultramega.showcaseitem.config.Config;
import com.ultramega.showcaseitem.network.ShareItemData;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ShowcaseItem.MODID)
public final class ShowcaseItem {
    public static final String MODID = "showcaseitem";

    public ShowcaseItem(final IEventBus modEventBus, final ModContainer modContainer) {
        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            final PayloadRegistrar registrar = event.registrar(MODID);
            registrar.playToServer(
                ShareItemData.TYPE,
                ShareItemData.STREAM_CODEC,
                ShareItemData::handle
            );
        });

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        private ClientModEvents() {
        }

        @SubscribeEvent
        public static void onClientSetup(final RegisterKeyMappingsEvent event) {
            event.registerCategory(ModKeyBindings.SHOWCASE_ITEM_CATEGORY);
            event.register(ModKeyBindings.SHOWCASE_ITEM_KEY);
        }
    }
}
