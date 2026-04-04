package com.ultramega.showcaseitem;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyMapping.Category SHOWCASE_ITEM_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ShowcaseItem.MODID, "category"));

    public static final KeyMapping SHOWCASE_ITEM_KEY = new KeyMapping(
            "key." + ShowcaseItem.MODID + ".showcaseitem",
            KeyConflictContext.GUI,
            KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            SHOWCASE_ITEM_CATEGORY
    );

    private ModKeyBindings() {
    }
}
