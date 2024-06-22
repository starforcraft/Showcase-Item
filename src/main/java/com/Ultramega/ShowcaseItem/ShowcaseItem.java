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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ShowcaseItem.MODID)
public class ShowcaseItem {
    public static final String MODID = "showcaseitem";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public ShowcaseItem(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            PayloadRegistrar registrar = event.registrar(MODID);
            registrar.playBidirectional(ShareItemData.TYPE, ShareItemData.STREAM_CODEC, ShareItemData::handle);
        });

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(final RegisterKeyMappingsEvent event) {
            event.register(ModKeyBindings.SHOWCASE_ITEM);
        }
    }
}
