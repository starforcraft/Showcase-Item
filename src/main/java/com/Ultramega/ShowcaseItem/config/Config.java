package com.ultramega.showcaseitem.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.ultramega.showcaseitem.ShowcaseItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber
public class Config {
    private static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec common_config;

    static {
        ShowcaseItemConfig.init(common_builder);
        common_config = common_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        ShowcaseItem.LOGGER.info("Loading config: " + path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        ShowcaseItem.LOGGER.info("Built config: " + path);
        file.load();
        ShowcaseItem.LOGGER.info("Loaded config: " + path);
        config.setConfig(file);
    }
}