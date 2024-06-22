package com.ultramega.showcaseitem;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyMapping SHOWCASE_ITEM = new KeyMapping(
            "key." + ShowcaseItem.MODID + ".showcaseitem",
            KeyConflictContext.GUI,
            KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category." + ShowcaseItem.MODID
    );
}