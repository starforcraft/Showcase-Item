package com.ultramega.showcaseitem;

import com.ultramega.showcaseitem.config.Config;
import com.ultramega.showcaseitem.message.ShowcaseItemNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("showcaseitem")
public class ShowcaseItem {
	public static final String MOD_ID = "showcaseitem";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public ShowcaseItem() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
		Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve("showcaseitem-common.toml").toString());

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ShowcaseItem::registerKeyBinding));

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		ShowcaseItemNetwork.setup();
	}

	public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
		event.register(ModKeyBindings.SHOWCASE_ITEM);
	}
}
