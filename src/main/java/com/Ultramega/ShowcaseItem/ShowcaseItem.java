package com.Ultramega.ShowcaseItem;

import com.Ultramega.ShowcaseItem.config.Config;
import com.Ultramega.ShowcaseItem.network.ModNetworkHandler;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("showcaseitem")
public class ShowcaseItem {
	public static final String MOD_ID = "showcaseitem";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final ModNetworkHandler NETWORK_HANDLER = new ModNetworkHandler();

	public ShowcaseItem() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
		Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve("showcaseitem-common.toml").toString());

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		NETWORK_HANDLER.init();
	}

	private void doClientStuff(FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(ModKeyBindings.SHOWCASE_ITEM);
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {

	}
}
